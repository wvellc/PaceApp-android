package net.paceapp.core.auth

import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuthException

// Maps raw Firebase/network auth errors to friendly, user-facing messages.
// Kotlin port of iOS AuthErrorMapper — used across login, OTP, email-link, and
// account-deletion reauth flows in place of raw exception messages.
object AuthErrorMapper {

    fun message(error: Throwable): String = when (error) {
        is FirebaseNetworkException ->
            "No internet connection. Please check your network and try again."

        is FirebaseAuthException -> when (error.errorCode) {
            "ERROR_INVALID_VERIFICATION_CODE" ->
                "That code isn't correct. Please check it and try again."
            "ERROR_SESSION_EXPIRED", "ERROR_CODE_EXPIRED" ->
                "That code has expired. Please request a new one."
            "ERROR_REQUIRES_RECENT_LOGIN", "ERROR_USER_TOKEN_EXPIRED" ->
                "For your security, please sign in again to continue."
            "ERROR_TOO_MANY_REQUESTS", "ERROR_QUOTA_EXCEEDED" ->
                "Too many attempts. Please try again later."
            "ERROR_INVALID_PHONE_NUMBER", "ERROR_MISSING_PHONE_NUMBER" ->
                "That phone number looks invalid. Please check it and try again."
            "ERROR_NETWORK_REQUEST_FAILED" ->
                "No internet connection. Please check your network and try again."
            "ERROR_APP_NOT_AUTHORIZED", "ERROR_MISSING_APP_CREDENTIAL", "ERROR_INVALID_APP_CREDENTIAL" ->
                "We couldn't verify this app for sign-in. Please try again."
            else -> error.localizedMessage ?: "Something went wrong. Please try again."
        }

        else -> error.localizedMessage ?: "Something went wrong. Please try again."
    }
}
