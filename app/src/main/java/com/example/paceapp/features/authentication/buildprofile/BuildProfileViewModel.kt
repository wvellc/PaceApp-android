package com.example.paceapp.features.authentication.buildprofile

import androidx.compose.runtime.snapshotFlow
import com.example.paceapp.core.base.BaseViewModel
import com.example.paceapp.features.authentication.buildprofile.BuildProfileContract.Effect
import com.example.paceapp.features.authentication.buildprofile.BuildProfileContract.Event
import com.example.paceapp.features.authentication.buildprofile.BuildProfileContract.State
import com.example.paceapp.features.authentication.buildprofile.domain.ValidateBuildProfileUseCase
import com.wvelabs.core_ui.components.imagepicker.ImagePickerAction
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class BuildProfileViewModel @Inject constructor() : BaseViewModel<State, Event, Effect>() {

    val validationUseCase = ValidateBuildProfileUseCase()

    override fun setInitialState() = State()
    override fun handleEvents(event: Event) {
        when (event) {
            is Event.Init -> initData()
            is Event.OnImagePickerAction -> handleImagePickerAction(event.action)
            is Event.OnBackClick -> handleOnBackClicked()
            is Event.OnSkipClick -> handleOnSkipClicked()
            is Event.OnNextClick -> handleNextButtonClicked()
        }
    }

    private fun initData() {
        if (currentState.isInitialized) return
        observeFields()
        setState { copy(isInitialized = true) }
    }

    private fun observeFields() {
        val validationFlow = snapshotFlow {
            validationUseCase(
                currentStep = currentState.currentStep,
                firstName = currentState.firstNameState.text.trim().toString(),
                lastName = currentState.lastNameState.text.trim().toString(),
            )
        }

        observeState(validationFlow) { isValid ->
            copy(isNextButtonEnabled = isValid)
        }
    }

    private fun handleImagePickerAction(action: ImagePickerAction) {
        when (action) {
            ImagePickerAction.Cancelled -> {
                //Do nothing
            }

            ImagePickerAction.Removed -> {
                setState { copy(profileImage = null) }
            }

            is ImagePickerAction.Selected -> setState {
                copy(profileImage = action.uri.toString())
            }
        }
    }

    private fun handleOnBackClicked() {

    }

    private fun handleOnSkipClicked() {

    }

    private fun handleNextButtonClicked() {

    }


    private fun saveUserDetails() {
        //TODO:Call API
        navigateToProfileSuccess()
    }

    private fun navigateToProfileSuccess() {
        setEffect { Effect.NavigateToProfileSuccess }
    }

}

