package com.example.paceapp.features.authentication.login

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.focus.FocusRequester
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.paceapp.core.domain.enums.LoginTypes
import com.example.paceapp.features.authentication.login.LoginContract.Effect
import com.example.paceapp.features.authentication.login.LoginContract.Event
import com.example.paceapp.features.authentication.login.components.LoginContent
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest

@Composable
fun LoginScreen(
    viewModel: LoginViewModel = hiltViewModel(),
    onBack: () -> Unit,
    onNavigateToWebview: (String, String?) -> Unit,
    onNavigateToVerifyOtp: (LoginTypes, String, String?) -> Unit,
) {
    val state by viewModel.state.collectAsState()
    BackHandler(enabled = true) {
        viewModel.setEvent(Event.OnBackClicked)
    }

    // Init view model
    LaunchedEffect(key1 = Unit) {
        viewModel.setEvent(Event.Init)
    }
    val emailFocus = remember { FocusRequester() }
    val phoneFocus = remember { FocusRequester() }
    // Handle one-time effects
    LaunchedEffect(key1 = Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is Effect.NavigateBack -> onBack()
                is Effect.NavigateToWebview -> onNavigateToWebview(effect.url, effect.title)
                is Effect.NavigateToVerifyOtp -> onNavigateToVerifyOtp(
                    effect.loginType,
                    effect.emailPhoneValue,
                    effect.countryCode
                )
                is Effect.RequestFocus -> {
                    // Small delay to ensure the keyboard can open after transition
                    delay(150)
                    when (effect.type) {
                        LoginTypes.EMAIL -> emailFocus.requestFocus()
                        LoginTypes.PHONE -> phoneFocus.requestFocus()
                    }
                }
            }
        }
    }

    // Render content
    LoginContent(
        state = state,
        onEvent = viewModel::setEvent,
        emailFocus = emailFocus,
        phoneFocus = phoneFocus,
    )
}

