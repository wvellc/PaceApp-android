package com.example.paceapp.session

import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey

object AppSessionKeys {
    val ACCESS_TOKEN = stringPreferencesKey("jwt_access_token")

    val USER_DETAILS = stringPreferencesKey("user_details")
    val CONFIG_DETAILS = stringPreferencesKey("config_details")


    // The list of keys to IGNORE when clearing the session
    val IGNORE_KEY_LIST = listOf(CONFIG_DETAILS)

    val TIMER_TARGET_TIMESTAMP = longPreferencesKey("timer_target_timestamp")

}