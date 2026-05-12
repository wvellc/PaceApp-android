package com.example.paceapp.features.authentication.buildprofile.domain

import com.example.paceapp.core.components.Validator
import com.example.paceapp.core.components.ValidatorType
import com.example.paceapp.core.garmin.state.GarminSdkState
import com.example.paceapp.features.authentication.buildprofile.BuildProfileContract.State
import com.example.paceapp.features.authentication.buildprofile.models.ProfileStep
import javax.inject.Inject

class ValidateBuildProfileUseCase @Inject constructor() {

    operator fun invoke(state: State): Boolean {

        // No validation needed, if step is marked as skippable in the sealed class,
        if (state.currentStep.isSkippable) return true

        // --- Step-Specific Rules ---
        return when (state.currentStep) {

            ProfileStep.AccountSetup -> {
                // Validate names
                val firstName = state.firstNameState.text.trim().toString()
                val lastName = state.lastNameState.text.trim().toString()
                val validFirstName = firstName.isNotEmpty() && Validator.validate(
                    firstName,
                    ValidatorType.Name
                ) == null
                val validLastName = lastName.isNotEmpty() && Validator.validate(
                    lastName,
                    ValidatorType.Name
                ) == null
                validFirstName && validLastName
            }

            ProfileStep.PairWatchInit -> {
                state.garminState == GarminSdkState.Ready
            }

            ProfileStep.SelectModel -> {
                // User must have tapped a watch model from the list
                state.selectedWatch != null
            }

            ProfileStep.SetGait -> {
                val hasWalking = state.walkingGait.value > 0f
                val hasRunning = state.runningGait.value > 0f
                hasWalking && hasRunning
            }

            ProfileStep.ConnectStrava -> {
                state.stravaLinkState.text.trim().isNotEmpty()
            }

            // These steps are purely informational or transitional
            // and require no user input validation to proceed.
            ProfileStep.PairWatchSuccess -> {
                true
            }

        }
    }
}