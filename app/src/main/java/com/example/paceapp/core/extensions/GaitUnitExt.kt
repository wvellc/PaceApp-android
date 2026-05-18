package com.example.paceapp.core.extensions

import androidx.annotation.StringRes
import com.example.paceapp.R
import com.example.paceapp.core.domain.models.GaitUnit

val GaitUnit.titleRes: Int
    @StringRes
    get() = when (this) {
    GaitUnit.FEET -> R.string.feet
      GaitUnit.METERS -> R.string.meters
    }
