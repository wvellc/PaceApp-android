package com.example.paceapp.features.authentication.otpsuccess

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.paceapp.features.authentication.login.LoginContract
import kotlinx.coroutines.flow.collectLatest

import com.example.paceapp.features.authentication.otpsuccess.OtpSuccessContract.Effect
import com.example.paceapp.features.authentication.otpsuccess.OtpSuccessContract.Event
import com.example.paceapp.features.authentication.otpsuccess.components.OtpSuccessContent

@Composable
fun OtpSuccessScreen(
    viewModel: OtpSuccessViewModel = hiltViewModel(),
    onBack: () -> Unit,
    onNavigateToBuildProfile: () -> Unit,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    BackHandler(enabled = true) {
        viewModel.setEvent(Event.OnBackClicked)
    }

    LaunchedEffect(key1 = Unit) {
        viewModel.effect.collectLatest { effect ->
            when (effect) {
                is Effect.NavigateBack -> onBack()
                is Effect.NavigateToBuildProfile -> onNavigateToBuildProfile()
            }
        }
    }

    OtpSuccessContent(
        state = state,
        onEvent = viewModel::setEvent
    )
}
