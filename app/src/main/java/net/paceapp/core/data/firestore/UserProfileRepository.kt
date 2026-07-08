package net.paceapp.core.data.firestore

import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.wvelabs.core_network.utils.AppLogger
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject
import javax.inject.Singleton

// Firestore-backed user profile + settings store (`users/{uid}`). Mirrors iOS
// UserProfileRepository. Settings (gait, intervalVibrate/Beep, distanceUnit) live
// as flat fields + nested `gait` map on the user document — never a subcollection.
interface UserProfileRepository {
    fun observeUser(uid: String): Flow<UserDocument?>
    suspend fun getUser(uid: String): UserDocument?
    suspend fun upsertUser(user: UserDocument)
    suspend fun updateGait(uid: String, gait: GaitDocument)
    suspend fun updateIntervalVibrate(uid: String, enabled: Boolean)
    suspend fun updateIntervalBeep(uid: String, enabled: Boolean)
    suspend fun updateDistanceUnit(uid: String, fullName: String)
    suspend fun touchLastSynced(uid: String)
}

private const val TAG = "FirestoreUsers"

@Singleton
class FirestoreUserProfileRepository @Inject constructor(
    private val db: FirebaseFirestore,
) : UserProfileRepository {

    private fun userRef(uid: String) = db.collection(FirestoreCollections.USERS).document(uid)

    override fun observeUser(uid: String): Flow<UserDocument?> = callbackFlow {
        val registration = userRef(uid).addSnapshotListener { snapshot, error ->
            if (error != null) {
                AppLogger.e("[$TAG] user listener failed", error)
                return@addSnapshotListener
            }
            trySend(snapshot?.takeIf { it.exists() }?.toObject(UserDocument::class.java))
        }
        awaitClose { registration.remove() }
    }

    override suspend fun getUser(uid: String): UserDocument? {
        val snapshot = userRef(uid).get().await()
        return snapshot.takeIf { it.exists() }?.toObject(UserDocument::class.java)
    }

    override suspend fun upsertUser(user: UserDocument) {
        userRef(user.uuid).set(user, SetOptions.merge()).await()
    }

    override suspend fun updateGait(uid: String, gait: GaitDocument) {
        userRef(uid).set(mapOf("gait" to gait), SetOptions.merge()).await()
    }

    override suspend fun updateIntervalVibrate(uid: String, enabled: Boolean) {
        userRef(uid).set(mapOf("intervalVibrate" to enabled), SetOptions.merge()).await()
    }

    override suspend fun updateIntervalBeep(uid: String, enabled: Boolean) {
        userRef(uid).set(mapOf("intervalBeep" to enabled), SetOptions.merge()).await()
    }

    override suspend fun updateDistanceUnit(uid: String, fullName: String) {
        userRef(uid).set(mapOf("distanceUnit" to fullName), SetOptions.merge()).await()
    }

    override suspend fun touchLastSynced(uid: String) {
        userRef(uid).set(mapOf("lastSyncedAt" to FieldValue.serverTimestamp()), SetOptions.merge()).await()
    }
}
