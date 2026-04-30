package com.example.paceapp.features.authentication.buildprofile

import androidx.compose.foundation.text.input.TextFieldState
import com.example.paceapp.features.authentication.buildprofile.domain.ProfileStep
import com.example.paceapp.features.authentication.buildprofile.domain.StepNavigateDirection
import com.wvelabs.core_ui.base.ViewEvent
import com.wvelabs.core_ui.base.ViewSideEffect
import com.wvelabs.core_ui.base.ViewState
import com.wvelabs.core_ui.components.imagepicker.ImagePickerAction

class BuildProfileContract {
    data class State(
        val isInitialized: Boolean = false,
        val isLoading: Boolean = false,
        val currentStep: ProfileStep = ProfileStep.AccountSetup,
        val direction: StepNavigateDirection = StepNavigateDirection.Forward,
        val isNextButtonEnabled: Boolean = false,
        val firstNameState: TextFieldState = TextFieldState(),
        val lastNameState: TextFieldState = TextFieldState(),
        val profileImage: String? = null,
    ) : ViewState

    sealed class Event : ViewEvent {
        data object Init : Event()
        data object OnBackClick : Event()
        data object OnNextClick : Event()
        data object OnSkipClick : Event()

        data class OnImagePickerAction(val action: ImagePickerAction) : Event()

    }

    sealed class Effect : ViewSideEffect {
        data object NavigateBack : Effect()

        data object NavigateToProfileSuccess : Effect()
    }
}
