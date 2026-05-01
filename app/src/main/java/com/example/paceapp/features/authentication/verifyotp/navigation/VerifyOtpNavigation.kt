package com.example.paceapp.features.authentication.verifyotp.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.example.paceapp.core.domain.enums.LoginTypes
import com.example.paceapp.features.authentication.verifyotp.VerifyOtpScreen
import kotlinx.serialization.Serializable

@Serializable
data class VerifyOtpRoute(
    val loginType: LoginTypes,
    val emailPhoneValue: String,
    val countryCode: String? = null
)

fun NavGraphBuilder.verifyOtpScreen(
    onNavigateToOtpSuccess: (LoginTypes) -> Unit,
    onBack: () -> Unit
) {
    composable<VerifyOtpRoute> {
        VerifyOtpScreen(
            onBack = onBack,
            onNavigateToOtpSuccess = onNavigateToOtpSuccess,
        )
    }
}
