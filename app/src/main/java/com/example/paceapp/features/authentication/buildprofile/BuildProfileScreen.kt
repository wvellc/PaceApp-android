package com.example.paceapp.features.authentication.buildprofile

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import kotlinx.coroutines.flow.collectLatest

import com.example.paceapp.features.authentication.buildprofile.BuildProfileContract.Effect
import com.example.paceapp.features.authentication.buildprofile.BuildProfileContract.Event
import com.example.paceapp.features.authentication.buildprofile.components.BuildProfileContent

@Composable
fun BuildProfileScreen(
    viewModel: BuildProfileViewModel = hiltViewModel(),
    onBack: () -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    // Init view model
    LaunchedEffect(key1 = Unit) {
          viewModel.setEvent(Event.Init)
    }

    // Handle one-time effects
    LaunchedEffect(key1 = Unit) {
        viewModel.effect.collectLatest { effect ->
            when (effect) {
                is Effect.NavigateBack -> onBack()
            }
        }
    }

    // Render content
    BuildProfileContent(
        state = state,
        onEvent = viewModel::setEvent
    )
}
