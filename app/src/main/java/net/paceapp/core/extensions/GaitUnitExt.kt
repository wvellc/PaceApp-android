package net.paceapp.core.extensions

import androidx.annotation.StringRes
import net.paceapp.R
import net.paceapp.core.domain.models.GaitUnit

val GaitUnit.titleRes: Int
    @StringRes
    get() = when (this) {
    GaitUnit.FEET -> R.string.feet
      GaitUnit.METERS -> R.string.meters
    }
