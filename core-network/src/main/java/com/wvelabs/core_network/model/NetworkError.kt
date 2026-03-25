package com.wvelabs.core_network.model

/**
 * The agnostic error model. Notice it does not inherit from `Exception`.
 * It is a pure data class, making it completely safe to pass to the UI layer.
 */
data class NetworkError(
    val message: String,
    val errorType: ErrorType,
    val code: Int? = null
) {
    companion object {
        /**
         * Factory method to map raw HTTP codes to our clean Enum.
         */
        fun fromStatusCode(code: Int?, message: String?): NetworkError {
            val type = when (code) {
                400 -> ErrorType.BAD_REQUEST
                401 -> ErrorType.UNAUTHORIZED
                403 -> ErrorType.FORBIDDEN
                404 -> ErrorType.NOT_FOUND
                405 -> ErrorType.METHOD_NOT_ALLOWED
                408 -> ErrorType.REQUEST_TIMEOUT
                409 -> ErrorType.CONFLICT
                422 -> ErrorType.UNPROCESSABLE_ENTITY
                500 -> ErrorType.INTERNAL_SERVER_ERROR
                in 501..599 -> ErrorType.SERVICE_UNAVAILABLE
                else -> ErrorType.UNKNOWN
            }
            
            // Fallback message if the backend didn't provide one
            return NetworkError(message ?: "An unexpected error occurred", type, code)
        }

        /**
         * Dedicated factory for local connectivity issues.
         */
        fun noInternet(message: String? = null) = NetworkError(
            message = message ?: "Please check your internet connection.",
            errorType = ErrorType.NO_INTERNET
        )
    }
}