package net.paceapp.core.data.repository

import net.paceapp.core.domain.models.UserData
import net.paceapp.core.domain.repositories.UserRepository
import net.paceapp.session.AppSessionManager
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepositoryImpl @Inject constructor(
    private val sessionManager: AppSessionManager
) : UserRepository {

    override suspend fun getUserDetails(): UserData? {
        return sessionManager.getUserDetails()
    }

    override suspend fun updateUserDetails(user: UserData) {
        sessionManager.setUserDetails(user)
        // Future Firebase: firestore.collection("users").document(user.id!!).set(user).await()
    }

    override fun observeUserDetails(): Flow<UserData?> {
        return sessionManager.observeUserDetails()
    }

    override suspend fun savePairedWatchId(watchId: String) {
        sessionManager.saveWatchId(watchId)
    }

    override suspend fun getPairedWatchId(): String? {
        return sessionManager.getWatchId()
    }

    override suspend fun clearPairedWatchId() {
        sessionManager.clearWatchId()
    }
}