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
        val sendOtpEnabled : Boolean = false,
    ) : ViewState

    sealed class Event : ViewEvent {
        data object Init : Event()
        object OnBackClicked : Event()
        data class OnLoginTypeSelected(val loginType: LoginTypes) : Event()

    }

    sealed class Effect : ViewSideEffect {
        data object NavigateBack : Effect()
    }
}
