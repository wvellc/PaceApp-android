package com.example.paceapp.features.main.tabhost

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import kotlinx.coroutines.flow.collectLatest

import com.example.paceapp.features.main.tabhost.TabHostContract.Effect
import com.example.paceapp.features.main.tabhost.TabHostContract.Event
import com.example.paceapp.features.main.tabhost.components.TabHostContent

@Composable
fun TabHostScreen(
    viewModel: TabHostViewModel = hiltViewModel(),
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
    TabHostContent(
        state = state,
        onEvent = viewModel::setEvent
    )
}
