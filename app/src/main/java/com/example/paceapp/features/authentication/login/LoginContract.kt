package com.example.paceapp.features.authentication.login

// Importing interfaces from your untouchable core library!
import com.example.paceapp.features.authentication.data.enums.LoginTypes
import com.wvelabs.core_ui.base.ViewEvent
import com.wvelabs.core_ui.base.ViewSideEffect
import com.wvelabs.core_ui.base.ViewState

class LoginContract {

    data class State(
        val isInitialized: Boolean = false,
        val isLoading: Boolean = false,
        val selectedLoginType: LoginTypes = LoginTypes.EMAIL,
    ) : ViewState

    sealed class Event : ViewEvent {
        data object Init : Event()
        data object OnBackClicked : Event()
        data object OnCountryCodeClick : Event()

        data class OnLoginTypeSelected(val loginType: LoginTypes) : Event()
        data class OnLoginClick(val value: String) : Event()
        data class ToWebview(val url: String) : Event()

    }

    sealed class Effect : ViewSideEffect {
        data object NavigateBack : Effect()
        data class NavigateToWebview(val url: String, val title: String? = null) : Effect()
    }
}
