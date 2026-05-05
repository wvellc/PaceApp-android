package com.example.paceapp.features.authentication.buildprofile

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.paceapp.features.authentication.buildprofile.BuildProfileContract.Effect
import com.example.paceapp.features.authentication.buildprofile.BuildProfileContract.Event
import com.example.paceapp.features.authentication.buildprofile.components.BuildProfileContent
import kotlinx.coroutines.flow.collectLatest

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
        viewModel.effect.collectLatest { effect ->
            when (effect) {
                is Effect.NavigateBack -> onBack()
                is Effect.NavigateToProfileCreated -> onNavigateToProfileCreated()
            }
        }
    }

    // Render content
    BuildProfileContent(
        state = state,
        onEvent = viewModel::setEvent
    )
}
