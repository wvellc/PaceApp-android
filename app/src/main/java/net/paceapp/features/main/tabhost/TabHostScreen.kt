package net.paceapp.features.main.tabhost

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
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
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current

    // This fires exactly once when the Tab Host enters the composition
    LaunchedEffect(Unit) {
        viewModel.restoreGarminConnection(context)
    }
    // Init view model
    LaunchedEffect(key1 = Unit) {
        viewModel.setEvent(Event.Init)
    }


    // Render content
    TabHostContent(
        state = state,
        onEvent = viewModel::setEvent,
        onBack = onBack,
        onNavigateToSettings = onNavigateToSettings,
        onNavigateToAnalyticsDetails = onNavigateToAnalyticsDetails,
        onNavigateToSetGait = onNavigateToSetGait,
        onNavigateToEditProfile = onNavigateToEditProfile,
        onNavigateToManageWatch = onNavigateToManageWatch,
        onNavigateToNotifications = onNavigateToNotifications,
        onNavigateToCreateEvent = onNavigateToCreateEvent,
    )
}
