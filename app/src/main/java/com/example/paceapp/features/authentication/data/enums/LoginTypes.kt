package com.example.paceapp.features.authentication.data.enums

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.example.paceapp.R
import kotlinx.serialization.Serializable

@Serializable
enum class LoginTypes {
    EMAIL,
    PHONE,
}

val LoginTypes.title: String
    @Composable
    get() = stringResource(
        id = when (this) {
            LoginTypes.EMAIL -> R.string.email
            LoginTypes.PHONE -> R.string.phone_number
        }
    )