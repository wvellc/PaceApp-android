package com.example.paceapp.features.main.analytics

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.paceapp.features.main.analytics.AnalyticsContract.Effect
import com.example.paceapp.features.main.analytics.AnalyticsContract.Event
import com.example.paceapp.features.main.analytics.components.AnalyticsContent
import com.example.paceapp.core.enums.AnalyticsMetricType

@Composable
fun AnalyticsScreen(
    viewModel: AnalyticsViewModel = hiltViewModel(),
    onBack: () -> Unit,
    onNavigateToAnalyticsDetails: (AnalyticsMetricType) -> Unit,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    // Init view model
    LaunchedEffect(key1 = Unit) {
        viewModel.setEvent(Event.Init)
    }

    // Handle one-time effects
    LaunchedEffect(key1 = Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is Effect.NavigateBack -> onBack()
                is Effect.NavigateToAnalyticsDetails -> onNavigateToAnalyticsDetails(effect.type)
            }

        }
    }

    // Render content
    AnalyticsContent(
        state = state,
        onEvent = viewModel::setEvent
    )
}
