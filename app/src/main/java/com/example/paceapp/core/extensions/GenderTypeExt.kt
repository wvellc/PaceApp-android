package com.example.paceapp.core.extensions

import androidx.annotation.StringRes
import com.example.paceapp.R
import com.example.paceapp.core.domain.enums.GenderTypes

val GenderTypes.titleRes: Int
    @StringRes
    get() = when (this) {
        GenderTypes.MALE -> R.string.male
        GenderTypes.FEMALE -> R.string.female
        GenderTypes.OTHER -> R.string.other
    }
