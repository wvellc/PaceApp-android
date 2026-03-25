package com.wvelabs.core_network.utils

import com.wvelabs.core_network.model.NetworkResult
import com.wvelabs.core_network.model.NetworkError
import com.wvelabs.core_network.handler.ErrorHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Executes an API call safely on the IO dispatcher.
 * * @param apiCall A suspend lambda containing the Retrofit/Ktor request.
 * @return A [NetworkResult] containing either the parsed data or a mapped [NetworkError].
 */
suspend fun <T> safeApiCall(apiCall: suspend () -> T): NetworkResult<T> {
    // We force this to run on the IO dispatcher so we never block the Main thread,
    // even if the user forgets to switch dispatchers in the ViewModel.
    return withContext(Dispatchers.IO) {
        try {
            // If the call succeeds, wrap the result in Success
            NetworkResult.Success(apiCall.invoke())
        } catch (e: Exception) {
            // If it crashes, send the Exception to our handler and wrap the result in Failure
            NetworkResult.Failure(ErrorHandler.handle(e))
        }
    }
}