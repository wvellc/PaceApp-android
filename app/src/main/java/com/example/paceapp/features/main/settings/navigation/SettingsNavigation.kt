package com.example.paceapp.features.main.settings.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable
import com.example.paceapp.features.main.settings.SettingsScreen

@Serializable
data object SettingsRoute

fun NavGraphBuilder.settingsScreen(
    onBack: () -> Unit,
    onNavigateToWebview: (String, String?) -> Unit
) {
    composable<SettingsRoute> {
        SettingsScreen(
            onBack = onBack,
            onNavigateToWebview = onNavigateToWebview
        )
    }
}
