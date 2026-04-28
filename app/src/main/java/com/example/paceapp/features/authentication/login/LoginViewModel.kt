package com.example.paceapp.features.authentication.login

// App-specific base classes and managers

// Screen imports
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.clearText
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.viewModelScope
import com.example.paceapp.R
import com.example.paceapp.config.AppWebUrls
import com.example.paceapp.core.base.BaseViewModel
import com.example.paceapp.core.providers.AppResourceProvider
import com.example.paceapp.core.data.enums.LoginTypes
import com.example.paceapp.features.authentication.login.LoginContract.Effect
import com.example.paceapp.features.authentication.login.LoginContract.Event
import com.example.paceapp.features.authentication.login.LoginContract.State
import com.example.paceapp.features.authentication.login.domain.ValidateLoginInputUseCase
import com.example.paceapp.features.authentication.login.models.LoginFormInput
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
            is Event.OnLoginClick -> handleOnLoginClicked()
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

    private fun handleOnLoginClicked() {
        //Safety check
        if (!currentState.isSendOTPEnabled) return

        //TODO:API call
        val loginValue = when (currentState.selectedLoginType) {
            LoginTypes.EMAIL -> currentState.emailState.text.trim().toString()
            LoginTypes.PHONE -> currentState.phoneState.text.trim().toString()
        }
        val countryCode = when (currentState.selectedLoginType) {
            currentState.selectedLoginType -> currentState.countryCode
            else -> null
        }
        setEffect {
            Effect.NavigateToVerifyOtp(
                emailPhoneValue = loginValue,
                loginType = currentState.selectedLoginType,
                countryCode = countryCode,
            )
        }
    }


    private fun handleWebviewNavigation(url: String) {
        val title = when (url) {
            AppWebUrls.TERM_CONDITIONS -> resourceProvider.getString(R.string.terms_of_service)
            AppWebUrls.PRIVACY_POLICY -> resourceProvider.getString(R.string.privacy_policy)
            else -> null
        }
        setEffect { Effect.NavigateToWebview(url, title) }
    }


}
