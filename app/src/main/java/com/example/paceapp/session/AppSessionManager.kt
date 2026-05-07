package com.example.paceapp.session

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.example.paceapp.core.domain.models.UserData
import com.wvelabs.core_network.di.ApplicationScope
import com.wvelabs.core_network.session.SessionCache
import com.wvelabs.core_network.session.SessionListener
import com.wvelabs.core_network.session.TimerCache
import com.wvelabs.core_network.utils.CoreDataStore
import com.wvelabs.core_network.utils.CryptoManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class AppSessionManager @Inject constructor(
    dataStore: DataStore<Preferences>,// Injected by Hilt
    private val cryptoManager: CryptoManager,
    @param:ApplicationScope private val appScope: CoroutineScope
) : CoreDataStore(dataStore), SessionCache, SessionListener, TimerCache {
    // --- INTERCEPTOR CONTRACTS ---
    private val _sessionExpiredEvent = MutableSharedFlow<Unit>(extraBufferCapacity = 1)
    val sessionExpiredEvent = _sessionExpiredEvent.asSharedFlow()
    private val useEncryption = true

    override suspend fun getAccessToken(): String? {
        val rawValue = readOnce(AppSessionKeys.ACCESS_TOKEN) ?: return null
        return if (useEncryption) {
            val decrypted = cryptoManager.decrypt(rawValue)
            decrypted.ifBlank { null }
        } else {
            rawValue
        }
    }

    suspend fun saveToken(token: String) {
        if (useEncryption) {
            writeEncrypted(AppSessionKeys.ACCESS_TOKEN, token, cryptoManager)
        } else {
            write(AppSessionKeys.ACCESS_TOKEN, token)
        }
    }

    suspend fun isAuthenticated(): Boolean = getAccessToken() != null

    // --- OBJECTS (JSON) ---
    // Storing a UserData
    suspend fun setUserDetails(user: UserData) {
        // Convert object to JSON String
        val jsonString = Json.encodeToString(UserData.serializer(), user)
        if (useEncryption) {
            writeEncrypted(AppSessionKeys.USER_DETAILS, jsonString, cryptoManager)
        } else {
            write(AppSessionKeys.USER_DETAILS, jsonString)
        }
    }

    suspend fun getUserDetails(): UserData? {
        val rawValue = readOnce(AppSessionKeys.USER_DETAILS) ?: return null
        // Decrypt if policy is enabled
        val jsonString = if (useEncryption) {
            cryptoManager.decrypt(rawValue)
        } else {
            rawValue
        }
        // Parse JSON
        return try {
            if (jsonString.isBlank()) null
            else Json.decodeFromString<UserData>(jsonString)
        } catch (e: Exception) {
            null // Handles mismatched encryption or corrupted JSON
        }
    }

    // Observe user details JSON
    fun observeUserDetails(): Flow<UserData?> {
        val rawFlow = if (useEncryption) {
            observeEncrypted(AppSessionKeys.USER_DETAILS, "", cryptoManager)
        } else {
            observe(AppSessionKeys.USER_DETAILS, "")
        }

        return rawFlow
            .map { data ->
                if (data.isBlank()) return@map null
                try {
                    Json.decodeFromString<UserData>(data)
                } catch (e: Exception) {
                    // Logic mismatch or corruption handling
                    null
                }
            }
            .flowOn(Dispatchers.IO)
            .distinctUntilChanged()
    }

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

    // --- LOGOUT LOGIC ---
    override fun onSessionExpired() {
        appScope.launch { clearSession() }
        _sessionExpiredEvent.tryEmit(Unit)
    }

    suspend fun clearSession() {
        // Wipes everything EXCEPT onboarding and config details
        clearAll(AppSessionKeys.IGNORE_KEY_LIST)
    }
}