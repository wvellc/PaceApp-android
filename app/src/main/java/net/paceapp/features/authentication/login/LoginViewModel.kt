package net.paceapp.features.authentication.login

// App-specific base classes and managers

// Screen imports
import android.app.Activity
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.clearText
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.viewModelScope
import net.paceapp.R
import net.paceapp.config.AppWebUrls
import net.paceapp.core.auth.AuthManager
import net.paceapp.core.base.BaseViewModel
import net.paceapp.core.providers.AppResourceProvider
import net.paceapp.core.domain.enums.LoginTypes
import net.paceapp.features.authentication.login.LoginContract.Effect
import net.paceapp.features.authentication.login.LoginContract.Event
import net.paceapp.features.authentication.login.LoginContract.State
import net.paceapp.features.authentication.login.domain.ValidateLoginInputUseCase
import net.paceapp.features.authentication.login.models.LoginFormInput
import com.wvelabs.core_ui.alerts.AppAlerts
import com.wvelabs.core_ui.alerts.MessageType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val resourceProvider: AppResourceProvider,
    private val authManager: AuthManager,
) : BaseViewModel<State, Event, Effect>() {
    private val validateLoginInputUseCase = ValidateLoginInputUseCase()

    override fun setInitialState() = State()

    override fun handleEvents(event: Event) {
        when (event) {
            is Event.Init -> initData()
            is Event.OnBackClicked -> {
                setEffect { Effect.NavigateBack }
            }

            is Event.OnLoginTypeSelected -> handleOnLoginTypeSelected(event.loginType)
            is Event.OnLoginClick -> handleOnLoginClicked(event.activity)
            is Event.ToWebview -> handleWebviewNavigation(event.url)
            is Event.OnCountrySelected -> handleOnCountrySelected(event.dialCode)
        }
    }


    private fun initData() {
        if (currentState.isInitialized) return
        if (isDebugMode) {
            setDummyData()
        }
        observeFields()
        setState { copy(isInitialized = true) }
    }

    private fun setDummyData() {
        setState {
            copy(
                emailState = TextFieldState("max@mailinator.com"),
                phoneState = TextFieldState("1234567890"),
                selectedLoginType = LoginTypes.EMAIL,
                countryCode = "+1",
            )
        }
    }

    private fun observeFields() {
        val formInputFlow = snapshotFlow {
            LoginFormInput(
                email = currentState.emailState.text.trim().toString(),
                phone = currentState.phoneState.text.trim().toString()
            )
        }.distinctUntilChanged()

        val validationFlow = combine(
            state.map { it.selectedLoginType }.distinctUntilChanged(),
            formInputFlow
        ) { loginType, input ->
            validateLoginInputUseCase(loginType, input.email, input.phone)
        }
        observeState(validationFlow) { isValid ->
            copy(isSendOTPEnabled = isValid)
        }
    }


    private fun handleOnLoginTypeSelected(loginType: LoginTypes) {
        setState { copy(selectedLoginType = loginType) }
        setEffect { Effect.RequestFocus(loginType) }
        viewModelScope.launch {
            delay(300)
            when (loginType) {
                LoginTypes.EMAIL -> currentState.phoneState.clearText()
                else -> currentState.emailState.clearText()
            }
        }

    }

    private fun handleOnCountrySelected(code: String) {
        setState { copy(countryCode = code) }
    }

    private fun handleOnLoginClicked(activity: Activity?) {
        //Safety check
        if (!currentState.isSendOTPEnabled) return
        when (currentState.selectedLoginType) {
            LoginTypes.EMAIL -> sendEmailLink()
            LoginTypes.PHONE -> sendPhoneOtp(activity)
        }
    }

    // Passwordless email link — Firebase sends a sign-in link; the app completes it
    // when reopened via the link (see MainActivity). Mirrors iOS email-link flow.
    private fun sendEmailLink() {
        val email = currentState.emailState.text.trim().toString()
        setState { copy(isLoading = true) }
        viewModelScope.launch {
            authManager.sendEmailLink(email)
                .onSuccess {
                    setState { copy(isLoading = false) }
                    currentState.emailState.clearText()
                    AppAlerts.showToast(
                        resourceProvider.getString(R.string.sign_in_link_sent, email),
                        type = MessageType.Info,
                    )
                }
                .onFailure {
                    setState { copy(isLoading = false) }
                    AppAlerts.showToast(it.message.orEmpty(), type = MessageType.Error)
                }
        }
    }

    // Phone OTP — Firebase sends an SMS code; on codeSent we advance to the OTP
    // screen. Auto-verified SMS signs in directly and also routes via the OTP screen.
    private fun sendPhoneOtp(activity: Activity?) {
        if (activity == null) {
            AppAlerts.showToast(resourceProvider.getString(R.string.something_went_wrong), type = MessageType.Error)
            return
        }
        val phoneE164 = currentState.countryCode + currentState.phoneState.text.trim()
        setState { copy(isLoading = true) }
        authManager.sendPhoneOtp(
            activity = activity,
            phoneE164 = phoneE164,
            onCodeSent = {
                setState { copy(isLoading = false) }
                navigateToVerifyOtp(phoneE164)
            },
            onError = { message ->
                setState { copy(isLoading = false) }
                AppAlerts.showToast(message, type = MessageType.Error)
            },
            onAutoVerified = {
                // Instant SMS retrieval already signed the user in; the OTP screen
                // detects the active session on Init and routes onward.
                setState { copy(isLoading = false) }
                navigateToVerifyOtp(phoneE164)
            },
        )
    }

    private fun navigateToVerifyOtp(phoneE164: String) {
        setEffect {
            Effect.NavigateToVerifyOtp(
                emailPhoneValue = phoneE164,
                loginType = LoginTypes.PHONE,
                countryCode = currentState.countryCode,
            )
        }
    }


    private fun handleWebviewNavigation(url: String) {
        val title = when (url) {
            AppWebUrls.TERM_OF_SERVICE -> resourceProvider.getString(R.string.terms_of_service)
            AppWebUrls.PRIVACY_POLICY -> resourceProvider.getString(R.string.privacy_policy)
            else -> null
        }
        setEffect { Effect.NavigateToWebview(url, title) }
    }


}
