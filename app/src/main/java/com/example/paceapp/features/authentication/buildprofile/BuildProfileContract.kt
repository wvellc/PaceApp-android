package com.example.paceapp.features.authentication.buildprofile

import androidx.compose.foundation.text.input.TextFieldState
import com.example.paceapp.features.authentication.buildprofile.domain.GaitPace
import com.example.paceapp.features.authentication.buildprofile.domain.GaitUnit
import com.example.paceapp.features.authentication.buildprofile.domain.ProfileStep
import com.example.paceapp.features.authentication.buildprofile.domain.StepNavigateDirection
import com.example.paceapp.features.authentication.buildprofile.domain.WatchModel
import com.wvelabs.core_ui.base.ViewEvent
import com.wvelabs.core_ui.base.ViewSideEffect
import com.wvelabs.core_ui.base.ViewState
import com.wvelabs.core_ui.components.imagepicker.ImagePickerAction

class BuildProfileContract {
    data class State(
        val isInitialized: Boolean = false,
        val isLoading: Boolean = false,
        val currentStep: ProfileStep = ProfileStep.AccountSetup,
        val isNextButtonEnabled: Boolean = false,
        // --- Step 1: Set Account ---
        val firstNameState: TextFieldState = TextFieldState(),
        val lastNameState: TextFieldState = TextFieldState(),
        val profileImage: String? = null,
        // --- Step 2, 3 & 4: Watch Pairing ---
        val selectedWatch: WatchModel? = null,

        // --- Step 5: Set Gait
        val walkingGait: GaitPace = GaitPace(value = 1.0f, unit = GaitUnit.METERS),
        val runningGait: GaitPace = GaitPace(value = 1.0f, unit = GaitUnit.METERS),

        // --- Step 6: Strava ---
        val stravaLinkState: TextFieldState = TextFieldState(),
    ) : ViewState

    sealed class Event : ViewEvent {
        data object Init : Event()
        data object OnBackClick : Event()
        data object OnNextClick : Event()
        data object OnSkipClick : Event()
        data class OnWalkingGaitChanged(val gaitPace: GaitPace) : Event()
        data class OnRunningGaitChanged(val gaitPace: GaitPace) : Event()
        data class OnImagePickerAction(val action: ImagePickerAction) : Event()

    }

    sealed class Effect : ViewSideEffect {
        data object NavigateBack : Effect()
        data object NavigateToProfileSuccess : Effect()
    }
}
