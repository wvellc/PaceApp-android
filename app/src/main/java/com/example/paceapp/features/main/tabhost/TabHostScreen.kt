package com.example.paceapp.features.main.tabhost

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.paceapp.core.enums.AnalyticsMetricType
import com.example.paceapp.features.main.tabhost.TabHostContract.Event
import com.example.paceapp.features.main.tabhost.components.TabHostContent

@Composable
fun TabHostScreen(
    viewModel: TabHostViewModel = hiltViewModel(),
    onBack: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToAnalyticsDetails: (AnalyticsMetricType) -> Unit,
    onNavigateToSetGait: () -> Unit,
    onNavigateToEditProfile: () -> Unit,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

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
    )
}
