package net.paceapp.features.authentication.buildprofile

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import net.paceapp.features.authentication.buildprofile.BuildProfileContract.Effect
import net.paceapp.features.authentication.buildprofile.BuildProfileContract.Event
import net.paceapp.features.authentication.buildprofile.components.BuildProfileContent
import com.wvelabs.core_ui.alerts.AppAlerts

@Composable
fun BuildProfileScreen(
    viewModel: BuildProfileViewModel = hiltViewModel(),
    onBack: () -> Unit,
    onNavigateToProfileCreated: () -> Unit,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    BackHandler(enabled = true) {
        viewModel.setEvent(Event.OnBackClick)
    }
    // Init view model
    LaunchedEffect(key1 = Unit) {
        viewModel.setEvent(Event.Init)
    }

    // Handle one-time effects
    LaunchedEffect(key1 = Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is Effect.NavigateBack -> onBack()
                is Effect.NavigateToProfileCreated -> onNavigateToProfileCreated()
                is Effect.ShowToast -> AppAlerts.showToast(effect.message, type = effect.type)
            }
        }
    }

    // Render content
    BuildProfileContent(
        state = state,
        onEvent = viewModel::setEvent
    )
}
