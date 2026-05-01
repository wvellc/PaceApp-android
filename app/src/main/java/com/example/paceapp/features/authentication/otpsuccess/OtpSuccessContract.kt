package com.example.paceapp.features.authentication.otpsuccess

import com.example.paceapp.core.domain.enums.LoginTypes
import com.wvelabs.core_ui.base.ViewEvent
import com.wvelabs.core_ui.base.ViewSideEffect
import com.wvelabs.core_ui.base.ViewState

class OtpSuccessContract {
    data class State(
        val isInitialized: Boolean = false,
        val isLoading: Boolean = false,
        val loginType: LoginTypes = LoginTypes.EMAIL,
    ) : ViewState

    sealed class Event : ViewEvent {
        data object Init : Event()
        data object OnBackClicked : Event()
        data object OnContinueClicked : Event()
    }

    sealed class Effect : ViewSideEffect {
        data object NavigateBack : Effect()
        data object NavigateToTabHost : Effect()
        data object NavigateToBuildProfile : Effect()
    }
}
