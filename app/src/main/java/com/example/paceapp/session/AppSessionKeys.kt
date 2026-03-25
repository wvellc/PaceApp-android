package com.example.paceapp.session

import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey

object AppSessionKeys {
    val ONBOARDING_STATUS = booleanPreferencesKey("onboarding_status")
    val IS_AUTHENTICATED = booleanPreferencesKey("is_authenticated")
    val ACCESS_TOKEN = stringPreferencesKey("jwt_access_token")
    
    // We will store complex objects (like UserData) as JSON Strings
    val USER_DETAILS = stringPreferencesKey("user_details")
    val CONFIG_DETAILS = stringPreferencesKey("config_details")

    // The list of keys to IGNORE when wiping the session (brilliant UX detail, by the way)
    val IGNORE_KEY_LIST = listOf(ONBOARDING_STATUS, CONFIG_DETAILS)

}