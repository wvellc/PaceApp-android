package net.paceapp.features.main.managewatch.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable
import net.paceapp.features.main.managewatch.ManageWatchScreen

@Serializable
data object ManageWatchRoute

fun NavGraphBuilder.managewatchScreen(
    onBack: () -> Unit
) {
    composable<ManageWatchRoute> {
        ManageWatchScreen(
            onBack = onBack
        )
    }
}
