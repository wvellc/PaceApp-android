package com.example.paceapp.features.authentication.login

// App-specific base classes and managers

// Screen imports
import com.example.paceapp.R
import com.example.paceapp.config.AppWebUrls
import com.example.paceapp.core.base.BaseViewModel
import com.example.paceapp.core.providers.AppResourceProvider
import com.example.paceapp.features.authentication.data.enums.LoginTypes
import com.example.paceapp.features.authentication.login.LoginContract.Effect
import com.example.paceapp.features.authentication.login.LoginContract.Event
import com.example.paceapp.features.authentication.login.LoginContract.State
import com.example.paceapp.session.AppSessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val sessionManager: AppSessionManager,
    private val resourceProvider: AppResourceProvider,
) : BaseViewModel<State, Event, Effect>() {

    override fun setInitialState() = State()

    override fun handleEvents(event: Event) {
        when (event) {
            is Event.Init -> initData()
            is Event.OnBackClicked -> {
                setEffect { Effect.NavigateBack }
            }

            is Event.OnLoginTypeSelected -> handleOnLoginTypeSelected(event.loginType)

            is Event.OnLoginClick -> handleOnLoginClicked(event.value)
            is Event.ToWebview -> handleWebviewNavigation(event.url)
            is Event.OnCountryCodeClick -> handleOnCountryCodeClick()

        }
    }


    private fun initData() {
        if (state.value.isInitialized) return
        setState { copy(isInitialized = true) }
    }

    private fun handleOnLoginTypeSelected(loginType: LoginTypes) {
        setState { copy(selectedLoginType = loginType) }
    }

    private fun handleOnLoginClicked(value: String) {}

    private fun handleWebviewNavigation(url: String) {
        val title = when (url) {
            AppWebUrls.TERM_CONDITIONS -> resourceProvider.getString(R.string.terms_of_service)
            AppWebUrls.PRIVACY_POLICY -> resourceProvider.getString(R.string.privacy_policy)
            else -> null
        }
        setEffect { Effect.NavigateToWebview(url, title) }
    }

    private fun handleOnCountryCodeClick() {

    }

}
