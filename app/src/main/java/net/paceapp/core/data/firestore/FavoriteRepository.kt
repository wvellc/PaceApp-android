package net.paceapp.core.data.firestore

import com.google.firebase.Timestamp
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject
import javax.inject.Singleton

// Firestore-backed favorites store. Mirrors iOS FirestoreFavoritesRepository so both
// phones interoperate over the shared `favorites` collection (auto-id docs holding
// userId/eventId/createdAt). No interface — directly injectable, matches iOS shape.
@Singleton
class FavoriteRepository @Inject constructor(
    private val db: FirebaseFirestore,
) {

    private fun favoritesCollection() = db.collection(FirestoreCollections.FAVORITES)

    // Query the single favorite doc for this (user, event) pair, if any.
    private fun favoriteQuery(userId: String, eventId: String) =
        favoritesCollection()
            .whereEqualTo("userId", userId)
            .whereEqualTo("eventId", eventId)
            .limit(1)

    // Toggle: delete the existing favorite (→ false) or create a new one (→ true).
    suspend fun toggleFavorite(userId: String, eventId: String): Boolean {
        val snapshot = favoriteQuery(userId, eventId).get().await()
        val existing = snapshot.documents.firstOrNull()
        return if (existing != null) {
            existing.reference.delete().await()
            false
        } else {
            favoritesCollection().add(
                mapOf(
                    "userId" to userId,
                    "eventId" to eventId,
                    "createdAt" to Timestamp.now(),
                )
            ).await()
            true
        }
    }

    // True when a favorite doc exists for this (user, event) pair.
    suspend fun isFavorited(userId: String, eventId: String): Boolean =
        !favoriteQuery(userId, eventId).get().await().isEmpty

    // All eventIds this user has favorited.
    suspend fun fetchFavoriteEventIds(userId: String): List<String> {
        val snapshot = favoritesCollection()
            .whereEqualTo("userId", userId)
            .get()
            .await()
        return snapshot.documents.mapNotNull { it.getString("eventId") }
    }

    // Hydrate favorited events: read events/{id} by document id, drop deleted ones.
    // whereIn caps at 10 ids per query, so chunk and flatten.
    suspend fun fetchFavoriteEvents(userId: String): List<EventDocument> {
        val ids = fetchFavoriteEventIds(userId)
        if (ids.isEmpty()) return emptyList()

        return ids.chunked(10).flatMap { chunk ->
            db.collection(FirestoreCollections.EVENTS)
                .whereIn(FieldPath.documentId(), chunk)
                .get()
                .await()
                .toEventDocuments()
        }.filter { it.status != EventStatusValue.DELETED }
    }

    // Delete every favorite doc owned by this user — part of full account deletion.
    // Best-effort per doc; a single failure shouldn't abort the wider delete flow.
    suspend fun deleteAllForUser(userId: String) {
        val snapshot = favoritesCollection().whereEqualTo("userId", userId).get().await()
        for (doc in snapshot.documents) {
            runCatching { doc.reference.delete().await() }
        }
    }

    // Live stream of favorited eventIds for UIs that want real-time updates.
    fun observeFavoriteEventIds(userId: String): Flow<List<String>> = callbackFlow {
        val registration = favoritesCollection()
            .whereEqualTo("userId", userId)
            .addSnapshotListener { snapshot, _ ->
                trySend(snapshot?.documents?.mapNotNull { it.getString("eventId") }.orEmpty())
            }
        awaitClose { registration.remove() }
    }
}
