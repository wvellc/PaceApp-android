package com.example.paceapp.features.authentication.buildprofile

import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.viewModelScope
import com.example.paceapp.core.base.BaseViewModel
import com.example.paceapp.core.garmin.GarminDeviceManager
import com.example.paceapp.features.authentication.buildprofile.BuildProfileContract.Effect
import com.example.paceapp.features.authentication.buildprofile.BuildProfileContract.Event
import com.example.paceapp.features.authentication.buildprofile.BuildProfileContract.State
import com.example.paceapp.features.authentication.buildprofile.domain.ProfileStep
import com.example.paceapp.features.authentication.buildprofile.domain.ValidateBuildProfileUseCase
import com.example.paceapp.session.AppSessionManager
import com.wvelabs.core_ui.components.imagepicker.ImagePickerAction
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BuildProfileViewModel @Inject constructor(
    val sessionManager: AppSessionManager,
    private val validationUseCase: ValidateBuildProfileUseCase,
    private val garminManager: GarminDeviceManager
) : BaseViewModel<State, Event, Effect>() {


    override fun setInitialState() = State()
    override fun handleEvents(event: Event) {
        when (event) {
            is Event.Init -> initData()
            is Event.OnImagePickerAction -> handleImagePickerAction(event.action)
            is Event.OnNextClick -> handleNextButtonClicked()
            is Event.OnBackClick -> handleOnBackClicked()
            is Event.OnSkipClick -> handleOnSkipClicked()
            is Event.OnWalkingGaitChanged -> setState { copy(walkingGait = event.gaitPace) }
            is Event.OnRunningGaitChanged -> setState { copy(runningGait = event.gaitPace) }
        }
    }

    private fun initData() {
        if (currentState.isInitialized) return
        // Start listening to the text fields
        observeFields()
        updateDataAndValidate { copy(isInitialized = true) }
    }

    /**
     * Reactively observe Compose state properties (like TextFieldState)
     * without polluting the Event queue with keystrokes.
     */
    private fun observeFields() {
        val validationFlow = snapshotFlow {
            //Fields to listen for changes
            listOf(
                currentState.currentStep,
                currentState.selectedWatch,
                currentState.firstNameState.text.toString(),
                currentState.lastNameState.text.toString(),
                currentState.stravaLinkState.text.toString()
            )
        }

        observeState(validationFlow) {
            // Re-run validation against the current state
            val isValid = validationUseCase(this)
            copy(isNextButtonEnabled = isValid)
        }
    }

    private fun updateDataAndValidate(update: State.() -> State) {
        setState {
            val newState = update(this)

            // Run the entire new state through the Use Case
            val isValid = validationUseCase(newState)

            // Return the final state with the button enablement updated
            newState.copy(isNextButtonEnabled = isValid)
        }
    }

    private fun handleImagePickerAction(action: ImagePickerAction) {
        when (action) {
            ImagePickerAction.Cancelled -> { /*Do nothing*/
            }

            ImagePickerAction.Removed -> {
                setState { copy(profileImage = null) }
            }

            is ImagePickerAction.Selected -> setState { copy(profileImage = action.uri.toString()) }
        }
    }

    private fun handleNextButtonClicked() {
        if (currentState.currentStep == ProfileStep.PairWatchInit) {
            initializeGarminAndProceed()
        } else {
            navigateTo(currentState.currentStep.nextStep)
        }
    }


    private fun handleOnBackClicked() {
        val previous = currentState.currentStep.previousStep
        if (previous != null) {
            navigateTo(previous)
        } else {
            setEffect { Effect.NavigateBack }
        }
    }

    private fun handleOnSkipClicked() {
        if (currentState.currentStep.isSkippable) {
            navigateTo(currentState.currentStep.skipStep)
        }
    }

    private fun initializeGarminAndProceed() {
        setState { copy(isLoading = true) }

        viewModelScope.launch {
            val isSuccess = garminManager.initializeGarminService()

            setState { copy(isLoading = false) }

            if (isSuccess) {
                navigateTo(ProfileStep.SelectModel)
            } else {

                navigateTo(ProfileStep.SetGait)
            }
        }
    }


    private fun navigateTo(targetStep: ProfileStep?) {
        if (targetStep == null) {
            saveUserDetails()
            return
        }
        setState { copy(currentStep = targetStep) }
    }

    private fun saveUserDetails() {
        //TODO:Call API
        navigateToProfileSuccess()
    }

    private fun navigateToProfileSuccess() {
        setEffect { Effect.NavigateToProfileSuccess }
    }

}

