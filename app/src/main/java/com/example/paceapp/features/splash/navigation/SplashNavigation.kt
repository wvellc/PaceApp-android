package com.example.paceapp.features.splash.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.example.paceapp.features.splash.SplashScreen
import kotlinx.serialization.Serializable

@Serializable
data object SplashRoute

fun NavGraphBuilder.splashScreen(
    onNavigateToLogin: () -> Unit,
    onNavigateToTabHost: () -> Unit,
    onNavigateToBuildProfile: () -> Unit,
) {
    composable<SplashRoute> {
        SplashScreen(
            onNavigateToLogin = onNavigateToLogin,
            onNavigateToTabHost = onNavigateToTabHost,
            onNavigateToBuildProfile = onNavigateToBuildProfile
        )
    }
}
