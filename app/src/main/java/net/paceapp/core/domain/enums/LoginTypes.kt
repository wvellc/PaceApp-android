package net.paceapp.core.domain.enums

import androidx.annotation.StringRes
import net.paceapp.R
import kotlinx.serialization.Serializable

@Serializable
enum class LoginTypes {
    EMAIL,
    PHONE,
}

