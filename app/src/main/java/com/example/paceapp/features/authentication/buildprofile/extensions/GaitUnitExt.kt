package com.example.paceapp.features.authentication.buildprofile.extensions

import androidx.annotation.StringRes
import com.example.paceapp.R

val com.example.paceapp.features.authentication.buildprofile.domain.GaitUnit.titleRes: Int
    @StringRes
    get() = when (this) {
        _root_ide_package_.com.example.paceapp.features.authentication.buildprofile.domain.GaitUnit.FEET -> R.string.feet
        _root_ide_package_.com.example.paceapp.features.authentication.buildprofile.domain.GaitUnit.METERS -> R.string.meters
    }
