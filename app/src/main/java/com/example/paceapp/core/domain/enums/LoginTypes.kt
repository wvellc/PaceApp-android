package com.example.paceapp.core.domain.enums

import androidx.annotation.StringRes
import com.example.paceapp.R
import kotlinx.serialization.Serializable

@Serializable
enum class LoginTypes {
    EMAIL,
    PHONE,
}

