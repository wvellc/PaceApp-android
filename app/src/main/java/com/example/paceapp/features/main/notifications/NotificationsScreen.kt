package com.example.paceapp.features.main.notifications

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.paceapp.R
import com.example.paceapp.features.main.notifications.NotificationsContract.Effect
import com.example.paceapp.features.main.notifications.NotificationsContract.Event
import com.example.paceapp.features.main.notifications.components.NotificationsContent
import com.wvelabs.core_ui.alerts.AppAlerts
import com.wvelabs.core_ui.alerts.MessageType
import com.wvelabs.core_ui.alerts.UiText

@Composable
fun NotificationsScreen(
    viewModel: NotificationsViewModel = hiltViewModel(),
    onBack: () -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    // Init view model
    LaunchedEffect(key1 = Unit) {
        viewModel.setEvent(Event.Init)
    }
    fun close(){}

    // Handle one-time effects
    LaunchedEffect(key1 = Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is Effect.NavigateBack -> onBack()
                is Effect.ShowClearAllAlert -> {
                    //Clear all nt
                    AppAlerts.showDialog(
                        title = UiText.StringResource(R.string.clear_all_alert_title),
                        text = UiText.StringResource(R.string.clear_all_alert_message),
                        type = MessageType.Warning,
                        confirmText = UiText.StringResource(R.string.no),
                        dismissText = UiText.StringResource(R.string.yes),
                        onConfirm = {
                        },
                        onDismiss = {
                            viewModel.setEvent(Event.OnClearAllNotifications)
                        },
                    )
                }
            }
        }
    }
    // Render content
    NotificationsContent(
        state = state,
        onEvent = viewModel::setEvent
    )
}
