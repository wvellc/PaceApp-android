package com.example.paceapp.features.authentication.otpsuccess.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.example.paceapp.core.data.enums.LoginTypes
import com.example.paceapp.features.authentication.otpsuccess.OtpSuccessScreen
import kotlinx.serialization.Serializable

@Serializable
data class OtpSuccessRoute(val loginType: LoginTypes)

fun NavGraphBuilder.otpSuccessScreen(
    onBack: () -> Unit,
    onNavigateToBuildProfile: () -> Unit,
    onNavigateToTabHost: () -> Unit,
) {
    composable<OtpSuccessRoute> {
        OtpSuccessScreen(
            onBack = onBack,
            onNavigateToTabHost = onNavigateToTabHost,
            onNavigateToBuildProfile = onNavigateToBuildProfile,
        )
    }
}
