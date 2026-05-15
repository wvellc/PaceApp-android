package com.example.paceapp.core.domain.usecases

import com.example.paceapp.core.enums.AuthDestination
import com.example.paceapp.core.domain.models.isProfileComplete
import com.example.paceapp.session.AppSessionManager
import javax.inject.Inject


class AuthRouteManager @Inject constructor(
    private val sessionManager: AppSessionManager
) {
    /**
     * Evaluates session and user state to determine the next screen.
     */
    suspend fun getNextDestination(): AuthDestination {
        val isAuthenticated = sessionManager.isAuthenticated()

        if (!isAuthenticated) {
            return AuthDestination.LOGIN
        }

        val user = sessionManager.getUserDetails()
        // If authenticated, check the user's profile status
        return if (user?.isProfileComplete == true) {
            AuthDestination.TAB_HOST
        } else {
            AuthDestination.BUILD_PROFILE
        }
    }
}