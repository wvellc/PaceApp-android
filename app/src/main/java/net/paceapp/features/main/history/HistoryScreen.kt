package net.paceapp.features.main.history

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import net.paceapp.features.main.history.HistoryContract.Effect
import net.paceapp.features.main.history.HistoryContract.Event
import net.paceapp.features.main.history.components.HistoryContent

@Composable
fun HistoryScreen(
    viewModel: HistoryViewModel = hiltViewModel(),
    onBack: () -> Unit,
    onNavigateToEventDetails: (String, String, String, String) -> Unit,
    onNavigateToDuplicateEvent: (String, String, String, String) -> Unit,
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
                is Effect.NavigateToEventDetails -> onNavigateToEventDetails(
                    effect.id,
                    effect.eventName,
                    effect.location,
                    effect.date
                )

                is Effect.NavigateToDuplicateEvent -> onNavigateToDuplicateEvent(
                    effect.id,
                    effect.eventName,
                    effect.location,
                    effect.date
                )
            }
        }
    }

    // Render content
    HistoryContent(
        state = state,
        onEvent = viewModel::setEvent
    )
}
