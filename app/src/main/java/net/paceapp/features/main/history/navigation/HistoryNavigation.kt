package net.paceapp.features.main.history.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable
import net.paceapp.features.main.history.HistoryScreen

@Serializable
data object HistoryRoute

fun NavGraphBuilder.historyScreen(
    onBack: () -> Unit,
    onNavigateToEventDetails: () -> Unit
) {
    composable<HistoryRoute> {
        HistoryScreen(
            onBack = onBack,
            onNavigateToEventDetails = onNavigateToEventDetails
        )
    }
}
