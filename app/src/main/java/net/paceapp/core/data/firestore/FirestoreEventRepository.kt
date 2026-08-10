package net.paceapp.core.data.firestore

import com.google.firebase.firestore.DocumentReference
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
            // Newest-activity-first: a freshly edited or synced run jumps to the top of
            // History (mirrors iOS ea060f7). Needs composite index userId+status+updatedAt
            // (already deployed in the shared thepaceapp project by iOS).
            .orderBy("updatedAt", Query.Direction.DESCENDING)
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

    // Live single-doc listener — an open Event Details screen re-renders whenever the
    // doc changes (e.g. a name/location edit or a watch-driven completion).
    override fun observeEvent(eventId: Int): Flow<EventDocument?> = callbackFlow {
        val registration = eventRef(eventId).addSnapshotListener { snapshot, error ->
            if (error != null) {
                AppLogger.e("[$TAG] event listener failed", error)
                return@addSnapshotListener
            }
            trySend(snapshot?.takeIf { it.exists() }?.toObject(EventDocument::class.java))
        }
        awaitClose { registration.remove() }
    }

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
        // pass rebuilds a full document stamping source="watch", createdAt=now, and
        // completedAt only when the payload currently looks completed. `source`/
        // `createdAt`/`completedAt` must survive every echo — otherwise a watch
        // reconnect that re-sends a finished run as active would revert its completion
        // (the "run history reverts after reconnect" bug). Three branches (mirrors iOS
        // FirestoreEventRepository.upsert):
        val ref = eventRef(document.id)
        val snapshot = runCatching { ref.get().await() }.getOrNull()
        val current = snapshot?.takeIf { it.exists() }?.toObject(EventDocument::class.java)
        when {
            // Decoded existing doc — carry the write-once fields forward. completedAt is
            // preserved (a re-sent active echo can't null out a real completion), but a
            // genuine first completion (stored null) still takes the new value.
            current != null -> {
                document.source = current.source
                document.createdAt = current.createdAt
                document.completedAt = current.completedAt ?: document.completedAt
                // A user-deleted event stays deleted — a later watch re-sync must not
                // resurrect it back to active/completed.
                if (current.status == EventStatusValue.DELETED) {
                    document.status = EventStatusValue.DELETED
                }
                ref.set(document, SetOptions.merge()).await()
            }
            // Confirmed brand-new doc — full merge, including the write-once fields.
            snapshot != null && !snapshot.exists() -> {
                ref.set(document, SetOptions.merge()).await()
            }
            // Read failed (offline/cold cache) or the stored doc didn't decode — a doc may
            // exist that we can't see, so never blindly rewrite the three write-once fields.
            else -> {
                writeSkippingImmutableFields(ref, document)
            }
        }
    }

    // Merge-writes every field EXCEPT the write-once source/createdAt/completedAt, so an
    // unreadable/offline upsert can't clobber them on a doc that may already exist. (The
    // Android POJO encoder writes explicit nulls, so a full merge with those nulled would
    // clobber; a map that omits the keys is required.) Mirrors iOS writeSkippingImmutableFields.
    private suspend fun writeSkippingImmutableFields(ref: DocumentReference, d: EventDocument) {
        val data = hashMapOf<String, Any?>(
            "id" to d.id,
            "userId" to d.userId,
            "status" to d.status,
            "name" to d.name,
            "location" to d.location,
            "scheduledAt" to d.scheduledAt,
            "activityType" to d.activityType,
            "distanceValue" to d.distanceValue,
            "measure" to d.measure,
            "goalTimeSeconds" to d.goalTimeSeconds,
            "lookBackIntervals" to d.lookBackIntervals,
            "avgPaceSeconds" to d.avgPaceSeconds,
            "avgHeartRate" to d.avgHeartRate,
            "elevationGain" to d.elevationGain,
            "effortPercentage" to d.effortPercentage,
            "actualTimeSeconds" to d.actualTimeSeconds,
            "actualDistance" to d.actualDistance,
            "timeVarianceSeconds" to d.timeVarianceSeconds,
            "paces" to d.paces,
            "completedSegments" to d.completedSegments,
            "syncStatus" to d.syncStatus,
            "updatedAt" to d.updatedAt,
            "deletedAt" to d.deletedAt,
            "segments" to d.segments,
            "routePolyline" to d.routePolyline,
            // Deliberately omit source, createdAt, completedAt (write-once).
        )
        ref.set(data, SetOptions.merge()).await()
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

    // Hard-delete every event doc for this user (full account deletion). Best-effort
    // per doc so one failure doesn't strand the rest — the Auth account delete follows.
    override suspend fun deleteAllForUser(userId: String) {
        val snapshot = eventsCollection().whereEqualTo("userId", userId).get().await()
        for (doc in snapshot.documents) {
            runCatching { doc.reference.delete().await() }
        }
    }
}
