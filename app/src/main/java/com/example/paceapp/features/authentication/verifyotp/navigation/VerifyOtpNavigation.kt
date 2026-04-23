package com.example.paceapp.features.authentication.verifyotp.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.example.paceapp.features.authentication.verifyotp.VerifyOtpScreen
import kotlinx.serialization.Serializable

@Serializable
data object VerifyOtpRoute

fun NavGraphBuilder.verifyOtpScreen(
) {
    composable<VerifyOtpRoute> {
        VerifyOtpScreen()
    }
}
