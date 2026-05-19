package com.example.paceapp.features.authentication.buildprofile.domain

import com.example.paceapp.core.domain.usecases.ValidateUserNamesUseCase
import com.example.paceapp.core.garmin.state.GarminSdkState
import com.example.paceapp.features.authentication.buildprofile.BuildProfileContract.State
import com.example.paceapp.features.authentication.buildprofile.models.ProfileStep
import javax.inject.Inject

class ValidateBuildProfileUseCase @Inject constructor(
    private val validateUserNames: ValidateUserNamesUseCase
) {

    operator fun invoke(state: State): Boolean {

        // No validation needed, if step is marked as skippable in the sealed class,
        if (state.currentStep.isSkippable) return true

        // --- Step-Specific Rules ---
        return when (state.currentStep) {
            ProfileStep.AccountSetup -> {
                // Delegate to the shared logic
                validateUserNames(
                    firstName = state.firstNameState.text.toString(),
                    lastName = state.lastNameState.text.toString()
                )
            }

            ProfileStep.PairWatchInit -> state.garminState == GarminSdkState.Ready
            ProfileStep.SelectModel -> state.selectedWatch != null
            ProfileStep.SetGait -> state.walkingGait.value > 0f && state.runningGait.value > 0f
            ProfileStep.ConnectStrava -> state.stravaLinkState.text.trim().isNotEmpty()
            ProfileStep.PairWatchSuccess -> true
        }
    }
}