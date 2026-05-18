package com.example.paceapp.features.main.profile

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.paceapp.features.main.profile.ProfileContract.Effect
import com.example.paceapp.features.main.profile.ProfileContract.Event
import com.example.paceapp.features.main.profile.components.ProfileContent
import com.wvelabs.core_ui.alerts.AppAlerts
import com.wvelabs.core_ui.alerts.MessageType

@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel = hiltViewModel(),
    onBack: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToSetGait: () -> Unit,
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
                is Effect.NavigateToSettings -> onNavigateToSettings()
                Effect.NavigateToManageWatch -> {
                    AppAlerts.showToast("Coming Soon", type =  MessageType.Warning)
                }

                Effect.NavigateToSetGait ->onNavigateToSetGait()
            }
        }
    }

    // Render content
    ProfileContent(
        state = state,
        onEvent = viewModel::setEvent
    )
}
