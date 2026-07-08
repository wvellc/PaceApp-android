package net.paceapp.core.data.firestore

import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.SetOptions
import com.wvelabs.core_network.utils.AppLogger
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject
import javax.inject.Singleton

private const val TAG = "FirestoreEvents"

@Singleton
class FirestoreEventRepository @Inject constructor(
    private val db: FirebaseFirestore,
) : EventRepository {

    private fun eventsCollection() = db.collection(FirestoreCollections.EVENTS)
    private fun eventRef(eventId: Int) = eventsCollection().document(eventId.toString())

    // MARK: - Observe

    override fun observeActiveEvents(userId: String): Flow<List<EventDocument>> = callbackFlow {
        // No scheduledAt >= today filter — an overdue but still-active event must
        // stay visible (matches iOS). Ordering: earliest scheduled first.
        val registration = eventsCollection()
            .whereEqualTo("userId", userId)
            .whereEqualTo("status", EventStatusValue.ACTIVE)
            .orderBy("scheduledAt")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    AppLogger.e("[$TAG] active listener failed", error)
                    return@addSnapshotListener
                }
                trySend(snapshot?.toEventDocuments().orEmpty())
            }
        awaitClose { registration.remove() }
    }

    override fun observeCompletedEvents(userId: String): Flow<List<EventDocument>> = callbackFlow {
        val registration = eventsCollection()
            .whereEqualTo("userId", userId)
            .whereEqualTo("status", EventStatusValue.COMPLETED)
            .orderBy("completedAt", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    AppLogger.e("[$TAG] completed listener failed", error)
                    return@addSnapshotListener
                }
                trySend(snapshot?.toEventDocuments().orEmpty())
            }
        awaitClose { registration.remove() }
    }

    // One-shot read of a single event document by id.
    override suspend fun getEvent(eventId: Int): EventDocument? =
        runCatching { eventRef(eventId).get().await().toObject(EventDocument::class.java) }.getOrNull()

    // MARK: - Write

    override suspend fun upsert(
        payload: Map<String, Any?>,
        isCompleted: Boolean,
        syncStatus: String,
        source: String,
        userId: String,
    ) {
        val document = EventDocumentMapper.document(
            payload = payload,
            userId = userId,
            isCompleted = isCompleted,
            syncStatus = syncStatus,
            source = source,
        )

        // Write-once fields. The same event round-trips app ⇄ watch many times; each
        // pass rebuilds a full document. `source` (who created it) and `createdAt`
        // (when) are set once at creation and must never change — otherwise a
        // phone-created event echoed by the watch could flip to "watch". `id` is the
        // document key, so inherently immutable. Carry the stored values forward.
        val ref = eventRef(document.id)
        val existing = runCatching { ref.get().await() }.getOrNull()
        val current = existing?.takeIf { it.exists() }?.toObject(EventDocument::class.java)
        if (current != null) {
            document.source = current.source
            document.createdAt = current.createdAt
        }

        // Platform note: iOS's Codable encoder omits nil fields on a merge write;
        // the Android POJO encoder writes explicit nulls. For reads this is
        // equivalent (missing and null both decode to null) and our queries only
        // key off always-present fields (status/scheduledAt/completedAt), so the
        // difference is harmless. merge:true still preserves unrelated fields.
        ref.set(document, SetOptions.merge()).await()
    }

    override suspend fun updateMetadata(eventId: Int, userId: String, name: String, location: String) {
        eventRef(eventId).set(
            mapOf(
                "name" to name,
                "location" to location,
                "updatedAt" to FieldValue.serverTimestamp(),
                "userId" to userId,
            ),
            SetOptions.merge(),
        ).await()
    }

    override suspend fun softDelete(eventId: Int, userId: String) {
        eventRef(eventId).set(
            mapOf(
                "status" to EventStatusValue.DELETED,
                "deletedAt" to FieldValue.serverTimestamp(),
                "updatedAt" to FieldValue.serverTimestamp(),
                "userId" to userId,
            ),
            SetOptions.merge(),
        ).await()
    }

    // MARK: - Watch seeding

    // One query → client-side partition by status. 1 read vs 3, no composite index.
    override suspend fun fetchAllEventPayloads(userId: String): ConnectIQEventSnapshot {
        val snapshot = eventsCollection().whereEqualTo("userId", userId).get().await()

        val active = mutableListOf<Map<String, Any?>>()
        val completed = mutableListOf<Map<String, Any?>>()
        val deletedIds = mutableListOf<Int>()

        for (doc in snapshot.documents) {
            val event = doc.toObject(EventDocument::class.java) ?: continue
            val payload = EventDocumentMapper.connectIQPayload(event)
            when (event.status) {
                EventStatusValue.ACTIVE -> active.add(payload)
                EventStatusValue.COMPLETED -> completed.add(payload)
                EventStatusValue.DELETED -> deletedIds.add(event.id)
            }
        }
        return ConnectIQEventSnapshot(active, completed, deletedIds)
    }
}
