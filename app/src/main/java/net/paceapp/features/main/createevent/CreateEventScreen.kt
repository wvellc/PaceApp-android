package net.paceapp.features.main.createevent

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel

import net.paceapp.features.main.createevent.CreateEventContract.Effect
import net.paceapp.features.main.createevent.CreateEventContract.Event
import net.paceapp.features.main.createevent.components.CreateEventContent

@Composable
fun CreateEventScreen(
    viewModel: CreateEventViewModel = hiltViewModel(),
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
    CreateEventContent(
        state = state,
        onEvent = viewModel::setEvent
    )
}
