package net.paceapp.features.main.tabhost.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable
import net.paceapp.core.enums.AnalyticsMetricType
import net.paceapp.features.main.tabhost.TabHostScreen

@Serializable
data object TabHostRoute

fun NavGraphBuilder.tabHostScreen(
    onBack: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToAnalyticsDetails: (AnalyticsMetricType) -> Unit,
    onNavigateToSetGait: () -> Unit,
    onNavigateToEditProfile: () -> Unit,
    onNavigateToManageWatch: () -> Unit,
    onNavigateToNotifications: () -> Unit,
    onNavigateToCreateEvent: () -> Unit,
    onNavigateToEventDetails: (String, String, String, String) -> Unit,
    onNavigateToDuplicateEvent: (String, String, String, String) -> Unit,
    onNavigateToFavorites: () -> Unit,
    onNavigateToStrava: () -> Unit,
) {
    composable<TabHostRoute> {
        TabHostScreen(
            onBack = onBack,
            onNavigateToSettings = onNavigateToSettings,
            onNavigateToAnalyticsDetails = onNavigateToAnalyticsDetails,
            onNavigateToSetGait = onNavigateToSetGait,
            onNavigateToEditProfile = onNavigateToEditProfile,
            onNavigateToManageWatch = onNavigateToManageWatch,
            onNavigateToNotifications = onNavigateToNotifications,
            onNavigateToCreateEvent = onNavigateToCreateEvent,
            onNavigateToEventDetails = onNavigateToEventDetails,
            onNavigateToDuplicateEvent = onNavigateToDuplicateEvent,
            onNavigateToFavorites = onNavigateToFavorites,
            onNavigateToStrava = onNavigateToStrava
        )
    }
}
