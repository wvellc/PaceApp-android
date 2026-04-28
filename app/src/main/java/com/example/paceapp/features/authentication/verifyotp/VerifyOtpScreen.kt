package com.example.paceapp.features.authentication.verifyotp

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.paceapp.features.authentication.data.enums.LoginTypes
import com.example.paceapp.features.authentication.verifyotp.VerifyOtpContract.Effect
import com.example.paceapp.features.authentication.verifyotp.VerifyOtpContract.Event
import com.example.paceapp.features.authentication.verifyotp.components.VerifyOtpContent
import kotlinx.coroutines.flow.collectLatest

@Composable
fun VerifyOtpScreen(
    viewModel: VerifyOtpViewModel = hiltViewModel(),
    onBack: () -> Unit,
    onNavigateToOtpSuccess: (LoginTypes) -> Unit,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val countdown by viewModel.countDown.collectAsStateWithLifecycle()
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
                is Effect.NavigateToOtpSuccess -> onNavigateToOtpSuccess(effect.loginTypes)
            }
        }
    }

    // Render content
    VerifyOtpContent(
        state = state,
        onEvent = viewModel::setEvent,
        otpDuration = countdown,
    )
}
