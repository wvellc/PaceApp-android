package com.example.paceapp.features.authentication.profilecreated

import com.wvelabs.core_ui.base.ViewEvent
import com.wvelabs.core_ui.base.ViewSideEffect
import com.wvelabs.core_ui.base.ViewState

class ProfileCreatedContract {
    data class State(
        val isInitialized: Boolean = false,
        val isLoading: Boolean = false
    ) : ViewState

    sealed class Event : ViewEvent {
        data object Init : Event()
        data object OnBackClick : Event()
       data object OnGetStartedClicked : Event()
    }

    sealed class Effect : ViewSideEffect {
        data object NavigateBack : Effect()
        data object NavigateToTabHost : Effect()
    }
}
