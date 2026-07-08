package net.paceapp.core.data.firestore

import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import java.util.Date
import javax.inject.Inject
import javax.inject.Singleton

// Read side of the shared `events` collection for analytics. Mirrors the iOS
// AnalyticsRepository query exactly so both apps aggregate the same records.
@Singleton
class AnalyticsRepository @Inject constructor(
    private val db: FirebaseFirestore,
) {

    // Completed events for a user in [from, to], oldest first — same shape iOS uses.
    suspend fun fetchCompletedEvents(
        userId: String,
        fromEpochMillis: Long,
        toEpochMillis: Long,
    ): List<EventDocument> {
        val snapshot = db.collection(FirestoreCollections.EVENTS)
            .whereEqualTo("userId", userId)
            .whereEqualTo("status", EventStatusValue.COMPLETED)
            .whereGreaterThanOrEqualTo("completedAt", Timestamp(Date(fromEpochMillis)))
            .whereLessThanOrEqualTo("completedAt", Timestamp(Date(toEpochMillis)))
            .orderBy("completedAt", Query.Direction.ASCENDING)
            .get()
            .await()

        return snapshot.documents.mapNotNull {
            runCatching { it.toObject(EventDocument::class.java) }.getOrNull()
        }
    }
}
