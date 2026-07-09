package net.paceapp.features.authentication.login

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.focus.FocusRequester
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import net.paceapp.core.domain.enums.LoginTypes
import net.paceapp.features.authentication.login.LoginContract.Effect
import net.paceapp.features.authentication.login.LoginContract.Event
import net.paceapp.features.authentication.login.components.LoginContent
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest

@Composable
fun LoginScreen(
    viewModel: LoginViewModel = hiltViewModel(),
    onBack: () -> Unit,
    onNavigateToWebview: (String, String?) -> Unit,
    onNavigateToVerifyOtp: (LoginTypes, String, String?, String) -> Unit,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    BackHandler(enabled = true) {
        viewModel.setEvent(Event.OnBackClicked)
    }

    // Init view model
    LaunchedEffect(key1 = Unit) {
        viewModel.setEvent(Event.Init)
    }

    // Navigate to the OTP screen once the SMS code has been sent. Driven by
    // lifecycle-aware state so it fires after the app resumes from the reCAPTCHA
    // activity, when the nav host is ready (a one-shot effect could be dropped).
    LaunchedEffect(state.pendingOtpPhone) {
        val phone = state.pendingOtpPhone ?: return@LaunchedEffect
        onNavigateToVerifyOtp(
            LoginTypes.PHONE,
            phone,
            state.countryCode,
            state.pendingOtpVerificationId.orEmpty(),
        )
        viewModel.setEvent(Event.OtpNavConsumed)
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
                    effect.countryCode,
                    ""
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

