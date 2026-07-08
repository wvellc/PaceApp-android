package net.paceapp.core.data.firestore

// Firestore collection paths — must match the iOS app exactly (shared thepaceapp backend).
object FirestoreCollections {
    const val USERS = "users"
    const val EVENTS = "events"
    const val FAVORITES = "favorites"
}

// Event lifecycle status stored in the `status` field of an event document.
// Mirrors iOS EventStatus (String raw values).
object EventStatusValue {
    const val ACTIVE = "active"
    const val COMPLETED = "completed"
    const val DELETED = "deleted"
}
