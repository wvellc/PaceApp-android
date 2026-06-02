package net.paceapp.features.main.editevent

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel

import net.paceapp.features.main.editevent.EditEventContract.Effect
import net.paceapp.features.main.editevent.EditEventContract.Event
import net.paceapp.features.main.editevent.components.EditEventContent

@Composable
fun EditEventScreen(
    viewModel: EditEventViewModel = hiltViewModel(),
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
    EditEventContent(
        state = state,
        onEvent = viewModel::setEvent
    )
}
