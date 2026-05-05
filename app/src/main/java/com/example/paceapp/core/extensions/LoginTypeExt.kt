package com.example.paceapp.core.extensions

import androidx.annotation.StringRes
import com.example.paceapp.R
import com.example.paceapp.core.domain.enums.LoginTypes


val LoginTypes.titleRes: Int
    @StringRes
    get() = when (this) {
        LoginTypes.EMAIL -> R.string.email
        LoginTypes.PHONE -> R.string.phone_number
    }
