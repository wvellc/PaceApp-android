package com.wvelabs.core_network.model

/**
 * A strictly typed enum representing every possible API and Network failure.
 * This makes it easy to write exhaustive `when` statements in your ViewModels.
 */
enum class ErrorType {
    BAD_REQUEST,          // 400: Client sent invalid data
    UNAUTHORIZED,         // 401: Token expired or invalid (Triggers auto-logout)
    FORBIDDEN,            // 403: User doesn't have permission
    NOT_FOUND,            // 404: Endpoint or resource missing
    METHOD_NOT_ALLOWED,   // 405: e.g., Sent a GET instead of a POST
    REQUEST_TIMEOUT,      // 408: Server took too long
    CONFLICT,             // 409: e.g., Trying to register an existing email
    UNPROCESSABLE_ENTITY, // 422: Validation error from the server
    INTERNAL_SERVER_ERROR,// 500: Backend crashed
    SERVICE_UNAVAILABLE,  // 503: Server is down for maintenance
    NO_INTERNET,          // Device has no connection or DNS failed
    UNKNOWN               // Fallback for weird edge cases
}