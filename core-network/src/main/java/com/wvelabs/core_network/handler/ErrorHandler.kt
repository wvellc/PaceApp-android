package com.wvelabs.core_network.handler

import com.wvelabs.core_network.model.NetworkError
import retrofit2.HttpException
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

object ErrorHandler {
    
    /**
     * Converts raw Java/Kotlin/Retrofit Throwables into our clean NetworkError.
     */
    fun handle(throwable: Throwable): NetworkError {
        return when (throwable) {
            // Server took too long to respond
            is SocketTimeoutException -> NetworkError.fromStatusCode(408, "Request timed out. Please try again.")
            
            // Device is offline or DNS resolution failed
            is UnknownHostException, is IOException -> NetworkError.noInternet()
            
            // Retrofit successfully talked to the server, but the server returned a 4xx or 5xx error
            is HttpException -> {
                val code = throwable.code()
                
                // Optional: If your backend returns a standard JSON error like { "message": "Invalid password" },
                // you can parse `throwable.response()?.errorBody()?.string()` here instead of using the default message.
                val backendMessage = throwable.message() 
                
                NetworkError.fromStatusCode(code, backendMessage)
            }
            
            // A totally unhandled crash (e.g., JSON parsing failed)
            else -> NetworkError.fromStatusCode(null, throwable.message ?: "Unknown error occurred.")
        }
    }
}