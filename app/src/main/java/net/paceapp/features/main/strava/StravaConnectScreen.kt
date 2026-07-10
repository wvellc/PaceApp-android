package net.paceapp.features.main.strava

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import net.paceapp.features.main.strava.StravaConnectContract.Effect
import net.paceapp.features.main.strava.StravaConnectContract.Event
import net.paceapp.features.main.strava.components.StravaConnectContent

@Composable
fun StravaConnectScreen(
    viewModel: StravaConnectViewModel = hiltViewModel(),
    onBack: () -> Unit,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current

    LaunchedEffect(key1 = Unit) {
        viewModel.setEvent(Event.Init)
    }

    LaunchedEffect(key1 = Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is Effect.NavigateBack -> onBack()
            }
        }
    }

    StravaConnectContent(
        state = state,
        onEvent = viewModel::setEvent,
        context = context,
    )
}
