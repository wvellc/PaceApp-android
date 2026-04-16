package com.example.paceapp.features.authentication.login

// Importing interfaces from your untouchable core library!
import com.wvelabs.core_ui.base.ViewEvent
import com.wvelabs.core_ui.base.ViewSideEffect
import com.wvelabs.core_ui.base.ViewState

class LoginContract {

    data class State(
        val isInitialized: Boolean = false,
        val isLoading: Boolean = false
    ) : ViewState

    sealed class Event : ViewEvent {
        data object Init : Event()
    }

    sealed class Effect : ViewSideEffect {
        // data object NavigateBack : Effect()
    }
}
