package com.wvelabs.core_network.interceptors

import com.wvelabs.core_network.session.SessionCache
import com.wvelabs.core_network.session.SessionListener
import com.wvelabs.core_network.config.NetworkConstants
import com.wvelabs.core_network.model.ErrorType
import com.wvelabs.core_network.model.NetworkError
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class AuthInterceptor @Inject constructor(
    private val sessionCache: SessionCache,
    private val sessionListener: SessionListener
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        // 1. Fetch the token securely
        val token = runBlocking { sessionCache.getAccessToken() }

        // 2. Attach the token to the header using our constants
        val requestBuilder = chain.request().newBuilder()
        if (!token.isNullOrEmpty()) {
            requestBuilder.addHeader(
                NetworkConstants.AUTHORIZATION,
                "${NetworkConstants.BEARER} $token"
            )
        }

        // 3. Execute the call
        val response = chain.proceed(requestBuilder.build())

        // 4. Global 401 Handler mapped through your exact ErrorType enum!
        val networkError = NetworkError.fromStatusCode(response.code, response.message)
        if (networkError.errorType == ErrorType.UNAUTHORIZED) {
            sessionListener.onSessionExpired()
        }

        return response
    }
}