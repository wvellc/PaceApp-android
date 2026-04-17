package com.example.paceapp.features.splash

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.paceapp.features.splash.SplashContract.Effect
import com.example.paceapp.features.splash.SplashContract.Event
import com.example.paceapp.features.splash.components.SplashContent
import kotlinx.coroutines.flow.collectLatest

@Composable
fun SplashScreen(
    viewModel: SplashViewModel = hiltViewModel(),
    onNavigateToLogin: () -> Unit,
    onNavigateToDashboard: () -> Unit,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
     // Init view model
    LaunchedEffect(key1 = Unit) {
        viewModel.setEvent(Event.Init)
    }

    LaunchedEffect(key1 = Unit) {
        viewModel.effect.collectLatest { effect ->
            when (effect) {
                is Effect.NavigateToLogin -> onNavigateToLogin()
                is Effect.NavigateToDashboard -> onNavigateToDashboard()

            }
        }
    }


    // Render content
    SplashContent(
        state = state,
        onEvent = viewModel::setEvent
    )
}
