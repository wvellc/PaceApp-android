package net.paceapp.features.main.settings.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable
import net.paceapp.features.main.settings.SettingsScreen

@Serializable
data object SettingsRoute

fun NavGraphBuilder.settingsScreen(
    onBack: () -> Unit,
    onNavigateToWebview: (String, String?) -> Unit,
    onNavigateToStrava: () -> Unit,
) {
    composable<SettingsRoute> {
        SettingsScreen(
            onBack = onBack,
            onNavigateToWebview = onNavigateToWebview,
            onNavigateToStrava = onNavigateToStrava,
        )
    }
}
