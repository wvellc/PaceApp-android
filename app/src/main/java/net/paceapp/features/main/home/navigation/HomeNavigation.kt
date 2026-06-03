package net.paceapp.features.main.home.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable
import net.paceapp.features.main.home.HomeScreen

@Serializable
data object HomeRoute

fun NavGraphBuilder.homeScreen(
    onBack: () -> Unit,
    onNavigateToNotifications: () -> Unit,
    onNavigateToCreateEvent: () -> Unit,
    onNavigateToManageWatch: () -> Unit,
    onNavigateToEventDetails: (String, String, String, String) -> Unit,
    onNavigateToFavorites: () -> Unit,
) {
    composable<HomeRoute> {
        HomeScreen(
            onBack = onBack,
            onNavigateToNotifications = onNavigateToNotifications,
            onNavigateToCreateEvent = onNavigateToCreateEvent,
            onNavigateToManageWatch = onNavigateToManageWatch,
            onNavigateToEventDetails = onNavigateToEventDetails,
            onNavigateToFavorites = onNavigateToFavorites
        )
    }
}
