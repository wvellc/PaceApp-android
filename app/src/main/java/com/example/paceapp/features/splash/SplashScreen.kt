package com.example.paceapp.features.splash

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.flow.collectLatest
import com.example.paceapp.features.splash.SplashContract.Event
import com.example.paceapp.features.splash.components.SplashContent

@Composable
fun SplashScreen(
    viewModel: SplashViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    // Init view model
    LaunchedEffect(key1 = Unit) {
        viewModel.setEvent(Event.Init)
    }

    // Handle one-time effects
    LaunchedEffect(key1 = Unit) {
        viewModel.effect.collectLatest { effect ->
            // when (effect) { ... }
        }
    }

    // Render content
    SplashContent(
        state = state,
        onEvent = viewModel::setEvent
    )
}
