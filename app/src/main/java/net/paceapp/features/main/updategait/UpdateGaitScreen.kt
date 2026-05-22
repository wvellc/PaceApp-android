package net.paceapp.features.main.updategait

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel

import net.paceapp.features.main.updategait.UpdateGaitContract.Effect
import net.paceapp.features.main.updategait.UpdateGaitContract.Event
import net.paceapp.features.main.updategait.components.UpdateGaitContent

@Composable
fun UpdateGaitScreen(
    viewModel: UpdateGaitViewModel = hiltViewModel(),
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
    UpdateGaitContent(
        state = state,
        onEvent = viewModel::setEvent
    )
}
