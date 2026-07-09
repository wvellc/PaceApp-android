package net.paceapp.core.data.firestore

import kotlinx.coroutines.flow.Flow

// Result of one fetchAllEventPayloads call — partitioned client-side by status.
// Used to seed the watch (sync_all / sync_request reply). Mirrors iOS ConnectIQEventSnapshot.
data class ConnectIQEventSnapshot(
    val activePayloads: List<Map<String, Any?>>,
    val completedPayloads: List<Map<String, Any?>>,
    val deletedIds: List<Int>,
)

// Firestore-backed event store. Mirrors iOS EventRepositoryProtocol +
// FirestoreEventRepository so both phones interoperate over the shared backend.
interface EventRepository {

    // Live streams of the signed-in user's events (no date filter on active — a
    // past-due active event stays visible, matching iOS observeActiveEvents).
    fun observeActiveEvents(userId: String): Flow<List<EventDocument>>
    fun observeCompletedEvents(userId: String): Flow<List<EventDocument>>

    // One-shot read of a single event by id (events/{id}). Null if missing/malformed.
    suspend fun getEvent(eventId: Int): EventDocument?

    // Upsert from a raw ConnectIQ wire payload. Preserves write-once id/source/createdAt.
    suspend fun upsert(
        payload: Map<String, Any?>,
        isCompleted: Boolean,
        syncStatus: String,
        source: String,
        userId: String,
    )

    // Metadata-only edit (name/location) from the phone UI.
    suspend fun updateMetadata(eventId: Int, userId: String, name: String, location: String)

    // Soft delete — sets status=deleted + deletedAt, retained for sync history.
    suspend fun softDelete(eventId: Int, userId: String)

    // One read → client-side partition, to seed the watch handshake.
    suspend fun fetchAllEventPayloads(userId: String): ConnectIQEventSnapshot

    // Hard-delete every event doc owned by this user — part of full account deletion
    // (not a soft delete; the account is going away entirely). Mirrors iOS deleteAccount.
    suspend fun deleteAllForUser(userId: String)
}
