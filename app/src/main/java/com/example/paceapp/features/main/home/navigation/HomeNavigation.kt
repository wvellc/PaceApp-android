package com.example.paceapp.features.main.home.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable
import com.example.paceapp.features.main.home.HomeScreen

@Serializable
data object HomeRoute

fun NavGraphBuilder.homeScreen(
    onBack: () -> Unit
) {
    composable<HomeRoute> {
        HomeScreen(
            onBack = onBack
        )
    }
}
