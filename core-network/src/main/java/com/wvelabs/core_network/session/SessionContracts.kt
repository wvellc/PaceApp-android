package com.wvelabs.core_network.session

/**
 * The network module uses this to ask the app for a token.
 */
interface SessionCache {
    suspend fun getAccessToken(): String?
}

/**
 * The network module triggers this when a 401 Unauthorized happens.
 */
interface SessionListener {
    fun onSessionExpired()
}