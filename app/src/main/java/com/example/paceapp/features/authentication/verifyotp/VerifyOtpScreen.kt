package com.example.paceapp.features.authentication.verifyotp

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import kotlinx.coroutines.flow.collectLatest
import com.example.paceapp.features.authentication.verifyotp.VerifyOtpContract.Event
import com.example.paceapp.features.authentication.verifyotp.components.VerifyOtpContent

@Composable
fun VerifyOtpScreen(
    viewModel: VerifyOtpViewModel = hiltViewModel()
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
    VerifyOtpContent(
        state = state,
        onEvent = viewModel::setEvent
    )
}
