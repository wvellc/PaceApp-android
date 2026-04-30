package com.example.paceapp.features.authentication.login.domain

import com.example.paceapp.core.components.Validator
import com.example.paceapp.core.components.ValidatorType
import com.example.paceapp.core.data.enums.LoginTypes
import javax.inject.Inject

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