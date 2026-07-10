package net.paceapp.features.main.settings

import androidx.activity.compose.LocalActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import net.paceapp.R
import net.paceapp.core.extensions.openAppNotificationSettings
import net.paceapp.core.extensions.openBrowser
import net.paceapp.features.main.settings.SettingsContract.Effect
import net.paceapp.features.main.settings.SettingsContract.Event
import net.paceapp.features.main.settings.components.DeleteReauthOverlays
import net.paceapp.features.main.settings.components.SettingsContent
import com.wvelabs.core_ui.alerts.AppAlerts
import com.wvelabs.core_ui.alerts.MessageType
import com.wvelabs.core_ui.alerts.UiText

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = hiltViewModel(),
    onBack: () -> Unit,
    onNavigateToWebview: (String, String?) -> Unit,
    onNavigateToStrava: () -> Unit,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current
    // Firebase phone re-verification (for account deletion) requires an Activity.
    val activity = LocalActivity.current
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
                is Effect.NavigateToStrava -> onNavigateToStrava()
                is Effect.ShowLogoutDialog -> AppAlerts.showDialog(
                    title = UiText.StringResource(R.string.logout_dialog_title),
                    text = UiText.StringResource(R.string.logout_dialog_message),
                    dismissText = UiText.StringResource(R.string.log_out),
                    confirmText = UiText.StringResource(R.string.keep_going),
                    cancelable = true,
                    type = MessageType.Warning,
                    onDismiss = {
                        viewModel.setEvent(Event.OnLogoutConfirm)
                    }
                )

                // Reworded delete confirmation — the destructive "Delete" button starts
                // re-authentication; "Cancel" keeps the account (mirrors iOS).
                is Effect.ShowDeleteAccountDialog -> AppAlerts.showDialog(
                    title = UiText.StringResource(R.string.delete_account_reauth_title),
                    text = UiText.StringResource(R.string.delete_account_reauth_message),
                    dismissText = UiText.StringResource(R.string.delete),
                    confirmText = UiText.StringResource(R.string.cancel),
                    cancelable = true,
                    type = MessageType.Warning,
                    onDismiss = {
                        viewModel.setEvent(Event.OnDeleteAccountConfirm(activity))
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

    // Re-auth OTP / email-wait sheets + blocking processing overlay.
    DeleteReauthOverlays(
        state = state,
        onEvent = viewModel::setEvent,
    )
}
