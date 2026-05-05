package com.example.paceapp.features.authentication.buildprofile.domain

import androidx.annotation.StringRes
import com.example.paceapp.R

val GaitUnit.titleRes: Int
    @StringRes
    get() = when (this) {
        GaitUnit.FEET -> R.string.feet
        GaitUnit.METERS -> R.string.meters
    }
