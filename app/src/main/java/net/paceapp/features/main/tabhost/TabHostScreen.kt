package net.paceapp.features.main.tabhost

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import net.paceapp.core.enums.AnalyticsMetricType
import net.paceapp.features.main.tabhost.TabHostContract.Event
import net.paceapp.features.main.tabhost.components.TabHostContent

@Composable
fun TabHostScreen(
    viewModel: TabHostViewModel = hiltViewModel(),
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
    // Init view model
    LaunchedEffect(key1 = Unit) {
        viewModel.setEvent(Event.Init)
    }


    // Render content
    TabHostContent(
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
        onNavigateToStrava = onNavigateToStrava,
    )
}
