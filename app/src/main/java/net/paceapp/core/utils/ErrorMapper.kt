package net.paceapp.core.utils

import java.io.IOException
// import com.google.firebase.FirebaseException
// import com.google.firebase.auth.FirebaseAuthException

object ErrorMapper {
    fun getMessage(e: Throwable): String {
        return when (e) {
            is IOException -> "Network error. Please check your internet connection."

            // TODO: Uncomment when Firebase is added
            // is FirebaseAuthException -> e.localizedMessage ?: "Authentication failed."
            // is FirebaseException -> "A server error occurred. Please try again."

            is IllegalArgumentException -> e.message ?: "Invalid input provided."

            // Fallback
            else -> e.localizedMessage ?: "Something went wrong. Please try again."
        }
    }
}