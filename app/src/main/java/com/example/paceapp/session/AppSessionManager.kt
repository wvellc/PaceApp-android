package com.example.paceapp.session

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.wvelabs.core_network.session.SessionCache
import com.wvelabs.core_network.session.SessionListener
import com.wvelabs.core_network.utils.CoreDataStore
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class AppSessionManager @Inject constructor(
    dataStore: DataStore<Preferences> // Injected by Hilt
) : CoreDataStore(dataStore), SessionCache, SessionListener {

    // --- INTERCEPTOR CONTRACTS ---

    override suspend fun getAccessToken(): String? {
        return readOnce(AppSessionKeys.ACCESS_TOKEN)
    }

    override fun onSessionExpired() {
        // Triggered by 401. You would emit an event here to navigate the user out.
    }

    // --- PRIMITIVES (Booleans, Strings) ---

    suspend fun setOnboardingStatus(isComplete: Boolean) {
        write(AppSessionKeys.ONBOARDING_STATUS, isComplete)
    }

    suspend fun getOnboardingStatus(): Boolean {
        return readOnce(AppSessionKeys.ONBOARDING_STATUS) ?: false
    }

    suspend fun saveToken(token: String) {
        write(AppSessionKeys.ACCESS_TOKEN, token)
        write(AppSessionKeys.IS_AUTHENTICATED, true)
    }

    // --- COMPLEX OBJECTS (JSON) ---
    // Example: Storing a UserData object

//    suspend fun setUserDetails(user: UserData) {
//        // Convert object to JSON String
//        val jsonString = Json.encodeToString(user)
//        write(AppSessionKeys.USER_DETAILS, jsonString)
//    }
//
//    suspend fun getUserDetails(): UserData? {
//        val jsonString = read(AppSessionKeys.USER_DETAILS) ?: return null
//        return try {
//            // Convert JSON String back to object
//            Json.decodeFromString<UserData>(jsonString)
//        } catch (e: Exception) {
//            null // Handle parsing errors safely
//        }
//    }

    // --- LOGOUT LOGIC ---
    suspend fun clearSession() {
        // Wipes everything EXCEPT onboarding and config details
        clearAll(AppSessionKeys.IGNORE_KEY_LIST)
    }
}