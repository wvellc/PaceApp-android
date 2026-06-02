package net.paceapp.core.enums

import androidx.annotation.StringRes
import net.paceapp.R

enum class DistanceUnits(
    @param:StringRes val titleRes: Int,
    @param:StringRes val unitNameRes: Int
) {
    KMS(R.string.kms, R.string.km_unit),
    MILES(R.string.miles, R.string.mi_unit)
}

