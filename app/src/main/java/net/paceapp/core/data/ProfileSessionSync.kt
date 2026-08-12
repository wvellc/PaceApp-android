package net.paceapp.core.data

import com.google.firebase.auth.FirebaseAuth
import com.wvelabs.core_network.di.ApplicationScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import net.paceapp.core.data.firestore.UserProfileRepository
import net.paceapp.core.mappers.mergeInto
import net.paceapp.session.AppSessionManager
import javax.inject.Inject
import javax.inject.Singleton

// Live-mirrors the Firestore `users/{uid}` doc into the local session so a profile change
// made on ANOTHER device refreshes this device's UI (Profile/Home/EditProfile all read the
// session, which is observable). Started/stopped from AuthManager's auth-state listener.
@Singleton
class ProfileSessionSync @Inject constructor(
    private val auth: FirebaseAuth,
    private val userProfileRepository: UserProfileRepository,
    private val sessionManager: AppSessionManager,
    @param:ApplicationScope private val appScope: CoroutineScope,
) {
    private var job: Job? = null

    // Idempotent — safe to call on every sign-in / auth-state emission.
    fun start() {
        if (job != null) return
        val uid = auth.currentUser?.uid ?: return
        job = userProfileRepository.observeUser(uid)
            .onEach { doc ->
                if (doc == null) return@onEach
                val current = sessionManager.getUserDetails() ?: return@onEach
                val merged = doc.mergeInto(current)
                // distinctUntilChanged on the session flow already de-dupes, but skip the
                // write when nothing changed to avoid needless DataStore churn.
                if (merged != current) sessionManager.setUserDetails(merged)
            }
            .launchIn(appScope)
    }

    fun stop() {
        job?.cancel()
        job = null
    }
}
