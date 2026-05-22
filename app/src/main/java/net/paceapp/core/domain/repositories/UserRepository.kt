package net.paceapp.core.domain.repositories

import net.paceapp.core.domain.models.UserData
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    suspend fun getUserDetails(): UserData?
    suspend fun updateUserDetails(user: UserData)
    fun observeUserDetails(): Flow<UserData?>
    
    suspend fun savePairedWatchId(watchId: String)
    suspend fun getPairedWatchId(): String?
    suspend fun clearPairedWatchId()
}