package com.example.paceapp.features.main.analyticsdetail

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel

import com.example.paceapp.features.main.analyticsdetail.AnalyticsDetailContract.Effect
import com.example.paceapp.features.main.analyticsdetail.AnalyticsDetailContract.Event
import com.example.paceapp.features.main.analyticsdetail.components.AnalyticsDetailContent

@Composable
fun AnalyticsDetailScreen(
    viewModel: AnalyticsDetailViewModel = hiltViewModel(),
    onBack: () -> Unit
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
            }
        }
    }

    // Render content
    AnalyticsDetailContent(
        state = state,
        onEvent = viewModel::setEvent
    )
}
