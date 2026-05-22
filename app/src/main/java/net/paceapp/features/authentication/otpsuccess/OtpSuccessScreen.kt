package net.paceapp.features.authentication.otpsuccess

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import kotlinx.coroutines.flow.collectLatest

import net.paceapp.features.authentication.otpsuccess.OtpSuccessContract.Effect
import net.paceapp.features.authentication.otpsuccess.OtpSuccessContract.Event
import net.paceapp.features.authentication.otpsuccess.components.OtpSuccessContent

@Composable
fun OtpSuccessScreen(
    viewModel: OtpSuccessViewModel = hiltViewModel(),
    onBack: () -> Unit,
    onNavigateToTabHost: () -> Unit,
    onNavigateToBuildProfile: () -> Unit,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    BackHandler(enabled = true) {
        viewModel.setEvent(Event.OnBackClicked)
    }

    LaunchedEffect(key1 = Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is Effect.NavigateBack -> onBack()
                is Effect.NavigateToTabHost -> onNavigateToTabHost()
                is Effect.NavigateToBuildProfile -> onNavigateToBuildProfile()
            }
        }
    }

    OtpSuccessContent(
        state = state,
        onEvent = viewModel::setEvent
    )
}
