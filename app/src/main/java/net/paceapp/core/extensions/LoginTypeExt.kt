package net.paceapp.core.extensions

import androidx.annotation.StringRes
import net.paceapp.R
import net.paceapp.core.domain.enums.LoginTypes


val LoginTypes.titleRes: Int
    @StringRes
    get() = when (this) {
        LoginTypes.EMAIL -> R.string.email
        LoginTypes.PHONE -> R.string.phone_number
    }
