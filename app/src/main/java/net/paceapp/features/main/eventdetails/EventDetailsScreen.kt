package net.paceapp.features.main.eventdetails

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.wvelabs.core_ui.alerts.AppAlerts
import com.wvelabs.core_ui.alerts.MessageType
import com.wvelabs.core_ui.alerts.UiText
import net.paceapp.R
import net.paceapp.features.main.eventdetails.EventDetailsContract.Effect
import net.paceapp.features.main.eventdetails.EventDetailsContract.Event
import net.paceapp.features.main.eventdetails.components.EventDetailsContent

@Composable
fun EventDetailsScreen(
    viewModel: EventDetailsViewModel = hiltViewModel(),
    onBack: () -> Unit,
    onNavigateToEventMap: () -> Unit,
    onNavigateToEditEvent: (String, String, String) -> Unit,
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
                is Effect.NavigateToEditEvent -> onNavigateToEditEvent(
                    effect.id,
                    effect.eventName,
                    effect.location
                )
                is Effect.NavigateToEventMap -> onNavigateToEventMap()

                is Effect.ShowDeleteEventDialog -> {
                    //Clear all nt
                    AppAlerts.showDialog(
                        title = UiText.StringResource(R.string.delete_event_title),
                        text = UiText.StringResource(R.string.delete_event_message),
                        type = MessageType.Warning,
                        confirmText = UiText.StringResource(R.string.no),
                        dismissText = UiText.StringResource(R.string.yes),
                        onConfirm = {
                        },
                        onDismiss = {
                            viewModel.setEvent(Event.OnDeleteEventConfirmation)
                        },
                    )
                }

            }
        }
    }

    // Render content
    EventDetailsContent(
        state = state,
        onEvent = viewModel::setEvent
    )
}
