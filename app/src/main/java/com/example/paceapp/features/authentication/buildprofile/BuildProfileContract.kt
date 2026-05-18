package com.example.paceapp.features.authentication.buildprofile

import android.content.Context
import androidx.compose.foundation.text.input.TextFieldState
import com.example.paceapp.core.domain.enums.GenderTypes
import com.example.paceapp.core.domain.models.GaitPace
import com.example.paceapp.core.extensions.getDefaultGaits
import com.example.paceapp.core.garmin.models.WatchModel
import com.example.paceapp.core.garmin.state.GarminSdkState
import com.example.paceapp.features.authentication.buildprofile.models.ProfileStep
import com.wvelabs.core_ui.base.ViewEvent
import com.wvelabs.core_ui.base.ViewSideEffect
import com.wvelabs.core_ui.base.ViewState

class BuildProfileContract {
    data class State(
        val isInitialized: Boolean = false,
        val isLoading: Boolean = false,
        val currentStep: ProfileStep = ProfileStep.AccountSetup,
        val isNextButtonEnabled: Boolean = false,
        // --- Step 1: Set Account ---
        val firstNameState: TextFieldState = TextFieldState(),
        val lastNameState: TextFieldState = TextFieldState(),
        val selectedGender: GenderTypes = GenderTypes.MALE,
        val profileImage: String? = null,

        // --- Step 2, 3 & 4: Watch Pairing ---
        val garminState: GarminSdkState = GarminSdkState.Uninitialized,
        val showGarminSetupDialog: Boolean = false,
        val watchList: List<WatchModel> = emptyList(),
        val selectedWatch: WatchModel? = null,

        // --- Step 5: Set Gait
        val walkingGait: GaitPace = selectedGender.getDefaultGaits().first,
        val runningGait: GaitPace = selectedGender.getDefaultGaits().second,

        // --- Step 6: Strava ---
        val stravaLinkState: TextFieldState = TextFieldState(),
    ) : ViewState

    sealed class Event : ViewEvent {
        data object Init : Event()
        data object OnBackClick : Event()
        data class OnNextClick(val context: Context) : Event()
        data object OnSkipClick : Event()
        data class SelectWatchModel(val device: WatchModel) : Event()
        data class OnWalkingGaitChanged(val gaitPace: GaitPace) : Event()
        data class OnRunningGaitChanged(val gaitPace: GaitPace) : Event()
        data class OnGarminDialogRetry(val context: Context) : Event()
        data class OnGenderSelected(val gender: GenderTypes) : Event()

        data object OnGarminDialogSkip : Event()
    }

    sealed class Effect : ViewSideEffect {
        data object NavigateBack : Effect()
        data object NavigateToProfileCreated : Effect()
    }
}
