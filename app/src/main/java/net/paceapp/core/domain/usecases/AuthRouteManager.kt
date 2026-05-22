package net.paceapp.core.domain.usecases

import net.paceapp.core.enums.AuthDestination
import net.paceapp.core.domain.models.isProfileComplete
import net.paceapp.session.AppSessionManager
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