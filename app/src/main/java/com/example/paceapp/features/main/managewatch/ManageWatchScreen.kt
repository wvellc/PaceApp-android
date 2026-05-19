package com.example.paceapp.features.main.managewatch

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.paceapp.features.main.managewatch.ManageWatchContract.Effect
import com.example.paceapp.features.main.managewatch.ManageWatchContract.Event
import com.example.paceapp.features.main.managewatch.components.ManageWatchContent
import com.wvelabs.core_ui.alerts.AppAlerts

@Composable
fun ManageWatchScreen(
    viewModel: ManageWatchViewModel = hiltViewModel(),
    onBack: () -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    BackHandler(enabled = true) {
        viewModel.setEvent(Event.OnBackClick)
    }

    // Init view model
    LaunchedEffect(key1 = Unit) {
        viewModel.setEvent(Event.Init)
    }

    // Handle one-time effects
    LaunchedEffect(key1 = Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is Effect.NavigateBack -> onBack()
                is Effect.ShowToast -> {
                    AppAlerts.showToast(effect.message, type = effect.type)
                }
            }
        }
    }

    // Render content
    ManageWatchContent(
        state = state,
        onEvent = viewModel::setEvent
    )
}
