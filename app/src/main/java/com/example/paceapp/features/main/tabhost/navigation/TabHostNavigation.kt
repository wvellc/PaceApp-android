package com.example.paceapp.features.main.tabhost.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable
import com.example.paceapp.features.main.tabhost.TabHostScreen

@Serializable
data object TabHostRoute
fun NavGraphBuilder.tabHostScreen(
    onBack: () -> Unit,
    onNavigateToSettings: () -> Unit
) {
    composable<TabHostRoute> {
        TabHostScreen(
            onBack = onBack,
            onNavigateToSettings= onNavigateToSettings
        )
    }
}
