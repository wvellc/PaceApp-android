package net.paceapp.core.domain.usecases

import net.paceapp.core.components.Validator
import net.paceapp.core.components.ValidatorType
import javax.inject.Inject

class ValidateUserNamesUseCase @Inject constructor() {

    operator fun invoke(firstName: String, lastName: String): Boolean {
        val trimmedFirst = firstName.trim()
        val trimmedLast = lastName.trim()

        val validFirstName = trimmedFirst.isNotEmpty() && 
            Validator.validate(trimmedFirst, ValidatorType.Name) == null
            
        val validLastName = trimmedLast.isNotEmpty() && 
            Validator.validate(trimmedLast, ValidatorType.Name) == null

        return validFirstName && validLastName
    }
}