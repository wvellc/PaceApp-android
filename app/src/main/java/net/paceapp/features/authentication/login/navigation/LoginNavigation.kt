package net.paceapp.features.authentication.login.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import net.paceapp.core.domain.enums.LoginTypes
import net.paceapp.features.authentication.login.LoginScreen
import kotlinx.serialization.Serializable

@Serializable
data object LoginRoute

fun NavGraphBuilder.loginScreen(
    onBack: () -> Unit,
    onNavigateToWebview: (String, String?) -> Unit,
    onNavigateToVerifyOtp: (LoginTypes, String, String?) -> Unit,
) {
    composable<LoginRoute> {
        LoginScreen(
            onBack = onBack,
            onNavigateToWebview = onNavigateToWebview,
            onNavigateToVerifyOtp = onNavigateToVerifyOtp
        )
    }
}
