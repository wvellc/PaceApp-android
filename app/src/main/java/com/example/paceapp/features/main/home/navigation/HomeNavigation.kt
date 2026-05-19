package com.example.paceapp.features.main.home.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.example.paceapp.features.main.home.HomeScreen
import kotlinx.serialization.Serializable

@Serializable
data object HomeRoute

fun NavGraphBuilder.homeScreen(
    onBack: () -> Unit,
    onNavigateToNotifications: () -> Unit,
) {
    composable<HomeRoute> {
        HomeScreen(
            onBack = onBack,
            onNavigateToNotifications = onNavigateToNotifications
        )
    }
}
