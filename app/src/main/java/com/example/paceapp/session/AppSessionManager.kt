package com.example.paceapp.session

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.example.paceapp.core.domain.models.UserData
import com.wvelabs.core_network.di.ApplicationScope
import com.wvelabs.core_network.session.SessionCache
import com.wvelabs.core_network.session.SessionListener
import com.wvelabs.core_network.session.TimerCache
import com.wvelabs.core_network.utils.CoreDataStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class AppSessionManager @Inject constructor(
    dataStore: DataStore<Preferences>,// Injected by Hilt
    @param:ApplicationScope private val appScope: CoroutineScope
) : CoreDataStore(dataStore), SessionCache, SessionListener, TimerCache {
    private val _sessionExpiredEvent = MutableSharedFlow<Unit>(extraBufferCapacity = 1)
    val sessionExpiredEvent = _sessionExpiredEvent.asSharedFlow()

    // --- INTERCEPTOR CONTRACTS ---
    override suspend fun getAccessToken(): String? {
        return readOnce(AppSessionKeys.ACCESS_TOKEN)
    }

    override fun onSessionExpired() {
        appScope.launch { clearSession() }
        _sessionExpiredEvent.tryEmit(Unit)
    }


    suspend fun saveToken(token: String) {
        write(AppSessionKeys.ACCESS_TOKEN, token)
    }

    suspend fun isAuthenticated(): Boolean = getAccessToken() != null


    // --- TIMER PERSISTENCE ---
    override suspend fun saveTimerTarget(timestamp: Long) {
        write(AppSessionKeys.TIMER_TARGET_TIMESTAMP, timestamp)
    }

    override suspend fun getTimerTarget(): Long {
        return readOnce(AppSessionKeys.TIMER_TARGET_TIMESTAMP) ?: 0L
    }

    override suspend fun clearTimerTarget() {
        delete(AppSessionKeys.TIMER_TARGET_TIMESTAMP)
    }


    // --- OBJECTS (JSON) ---
    // Storing a UserData
    suspend fun setUserDetails(user: UserData) {
        // Convert object to JSON String
        val jsonString = Json.encodeToString(UserData.serializer(), user)
        write(AppSessionKeys.USER_DETAILS, jsonString)
    }

    suspend fun getUserDetails(): UserData? {
        val jsonString = readOnce(AppSessionKeys.USER_DETAILS) ?: return null
        return try {
            // Convert JSON String back to object
            Json.decodeFromString<UserData>(jsonString)
        } catch (e: Exception) {
            null // Handle parsing errors safely
        }
    }

    // Observe user details JSON
    fun observeUserDetails(): Flow<UserData?> =
        observe(AppSessionKeys.USER_DETAILS, "")
            .map { jsonString ->
                // 2. Safely map the String back to UserData
                if (jsonString.isBlank()) {
                    null
                } else {
                    try {
                        Json.decodeFromString<UserData>(jsonString)
                    } catch (e: Exception) {
                        // Log error if needed: AppLogger.e("Failed to parse UserData", e)
                        null
                    }
                }
            }


    // --- LOGOUT LOGIC ---
    suspend fun clearSession() {
        // Wipes everything EXCEPT onboarding and config details
        clearAll(AppSessionKeys.IGNORE_KEY_LIST)
    }
}