package net.paceapp.features.authentication.verifyotp.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import net.paceapp.core.domain.enums.LoginTypes
import net.paceapp.features.authentication.verifyotp.VerifyOtpScreen
import kotlinx.serialization.Serializable

@Serializable
data class VerifyOtpRoute(
    val loginType: LoginTypes,
    val emailPhoneValue: String,
    val countryCode: String? = null,
    // Firebase phone verificationId carried from Login (mirrors iOS). Empty for email.
    val verificationId: String = ""
)

fun NavGraphBuilder.verifyOtpScreen(
    onBack: () -> Unit,
    onNavigateToTabHost: () -> Unit,
    onNavigateToBuildProfile: () -> Unit
) {
    composable<VerifyOtpRoute> {
        VerifyOtpScreen(
            onBack = onBack,
            onNavigateToTabHost = onNavigateToTabHost,
            onNavigateToBuildProfile = onNavigateToBuildProfile,
        )
    }
}
