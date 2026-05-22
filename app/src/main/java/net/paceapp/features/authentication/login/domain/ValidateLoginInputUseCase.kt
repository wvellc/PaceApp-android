package net.paceapp.features.authentication.login.domain

import net.paceapp.core.components.Validator
import net.paceapp.core.components.ValidatorType
import net.paceapp.core.domain.enums.LoginTypes

class ValidateLoginInputUseCase{
    operator fun invoke(
        type: LoginTypes,
        email: String,
        phone: String
    ): Boolean {
        val value = when (type) {
            LoginTypes.EMAIL -> email.trim()
            LoginTypes.PHONE -> phone.trim()
        }
        val validator = when (type) {
            LoginTypes.EMAIL -> ValidatorType.Email
            LoginTypes.PHONE -> ValidatorType.Phone
        }
        return value.isNotEmpty() && Validator.validate(value, validator) == null

    }
}