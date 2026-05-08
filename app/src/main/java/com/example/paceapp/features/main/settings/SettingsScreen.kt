package com.example.paceapp.features.main.settings

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.paceapp.R
import com.example.paceapp.core.extensions.openAppNotificationSettings
import com.example.paceapp.core.extensions.openBrowser
import com.example.paceapp.features.main.settings.SettingsContract.Effect
import com.example.paceapp.features.main.settings.SettingsContract.Event
import com.example.paceapp.features.main.settings.components.SettingsContent
import com.wvelabs.core_ui.alerts.AppAlerts
import com.wvelabs.core_ui.alerts.MessageType
import com.wvelabs.core_ui.alerts.UiText

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = hiltViewModel(),
    onBack: () -> Unit,
    onNavigateToWebview: (String, String?) -> Unit,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current
    // Init view model
    LaunchedEffect(key1 = Unit) {
        viewModel.setEvent(Event.Init)
    }

    // Handle one-time effects
    LaunchedEffect(key1 = Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is Effect.NavigateBack -> onBack()
                is Effect.NavigateToWebview -> onNavigateToWebview(effect.url, effect.title)
                is Effect.NavigateToDeveloperWebsite -> context.openBrowser(effect.url)
                is Effect.NavigateToNotifications -> context.openAppNotificationSettings()
                is Effect.ShowLogoutDialog -> AppAlerts.showDialog(
                    title = UiText.StringResource(R.string.logout_dialog_title),
                    text = UiText.StringResource(R.string.logout_dialog_message),
                    dismissText = UiText.StringResource(R.string.log_out),
                    confirmText =UiText.StringResource(R.string.keep_going),
                    cancelable = true,
                    type = MessageType.Warning,
                    onDismiss = {
                        viewModel.setEvent(Event.OnLogoutConfirm)
                    }
                )
            }
        }
    }

    // Render content
    SettingsContent(
        state = state,
        onEvent = viewModel::setEvent
    )
}
