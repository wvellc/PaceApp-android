package net.paceapp.core.data.firestore

import com.google.firebase.Timestamp
import com.google.firebase.firestore.IgnoreExtraProperties

// Firestore document model for a persisted run/walk event.
// Field names MUST match the iOS EventDocument exactly — both apps read/write the
// same `events/{id}` documents in the shared thepaceapp project. All properties are
// `var` with defaults so the Firestore POJO mapper can instantiate + populate them.
//
// Document id is always String(id). `id` is also stored as a field (iOS writes it),
// so we read it from the field rather than via @DocumentId.
@IgnoreExtraProperties
data class EventDocument(
    var id: Int = 0,
    var userId: String = "",
    var status: String = EventStatusValue.ACTIVE,
    var name: String = "",
    var location: String = "",
    var scheduledAt: Timestamp? = null,
    var completedAt: Timestamp? = null,
    var activityType: String = "Run",
    var distanceValue: Double = 0.0,
    var measure: String = "Miles",
    var goalTimeSeconds: Int = 0,
    var lookBackIntervals: Int = 1,
    var avgPaceSeconds: Int? = null,
    var avgHeartRate: Int? = null,
    var elevationGain: Double? = null,
    var effortPercentage: Double? = null,
    var actualTimeSeconds: Int? = null,
    var actualDistance: Double? = null,
    var timeVarianceSeconds: Int? = null,
    // Per-interval pace, seconds each. Firestore returns integers as Long, so keep
    // the field Long-typed to survive POJO deserialization; convert at use sites.
    var paces: List<Long>? = null,
    // Watch actuals — mixed numeric types; kept generic like iOS FirestoreFlexibleValue.
    var completedSegments: List<Map<String, Any?>>? = null,
    var syncStatus: String = "synced",
    var source: String = "watch",
    var createdAt: Timestamp? = null,
    var updatedAt: Timestamp? = null,
    var deletedAt: Timestamp? = null,
    // Embedded segment array (NOT a subcollection) — same as iOS.
    var segments: List<SegmentDocument>? = null,
    // Full-route GPS trace, Google encoded-polyline format.
    var routePolyline: String? = null,
) {
    val firestoreDocumentId: String get() = id.toString()
    val eventStatus: String get() = status
}

// Embedded segment — matches iOS RunSegment's Firestore shape (CodingKeys map
// app `id` → `index`, and store `goalTimeSeconds` collapsed from H/M/S).
@IgnoreExtraProperties
data class SegmentDocument(
    var index: Int = 0,
    var distance: Double = 0.0,
    var goalTimeSeconds: Int = 0,
    var completedAt: Timestamp? = null,
    var actualTimeSeconds: Int? = null,
)
