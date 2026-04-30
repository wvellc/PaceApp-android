package com.example.paceapp.features.authentication.buildprofile.domain

import com.example.paceapp.core.components.Validator
import com.example.paceapp.core.components.ValidatorType

class ValidateBuildProfileUseCase {
    operator fun invoke(
        currentStep: ProfileStep,
        firstName: String,
        lastName: String,
    ): Boolean {
        //Do not check validation for skippable fields
        if (currentStep.isSkippable) return true


        when (currentStep) {
            //Account step validation
            ProfileStep.AccountSetup -> {
                val validFirstName = firstName.isNotEmpty() && Validator.validate(
                    firstName,
                    ValidatorType.Name
                ) == null

                val validLastName = lastName.isNotEmpty() && Validator.validate(
                    lastName,
                    ValidatorType.Name
                ) == null

                return validLastName && validFirstName
            }

            //Pair watch validation
            ProfileStep.PairWatchInit -> {}

            ProfileStep.SelectModel -> {}

            ProfileStep.PairSelectedWatch -> {}

            ProfileStep.SetGait -> {}

            ProfileStep.ConnectStrava -> {}
        }

        return true
    }
}