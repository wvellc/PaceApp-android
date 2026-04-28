package com.example.paceapp.features.authentication.buildprofile

import com.example.paceapp.features.authentication.buildprofile.domain.ProfileStep
import com.wvelabs.core_ui.base.ViewEvent
import com.wvelabs.core_ui.base.ViewSideEffect
import com.wvelabs.core_ui.base.ViewState

class BuildProfileContract {
    data class State(
        val isInitialized: Boolean = false,
        val isLoading: Boolean = false,
        val currentStep: ProfileStep = ProfileStep.AccountSetup,
        val isNextButtonEnabled: Boolean = false,
    ) : ViewState

    sealed class Event : ViewEvent {
        data object Init : Event()
        data object OnBackClick : Event()
        data object OnNextClick : Event()
    }

    sealed class Effect : ViewSideEffect {
        data object NavigateBack : Effect()
    }
}
