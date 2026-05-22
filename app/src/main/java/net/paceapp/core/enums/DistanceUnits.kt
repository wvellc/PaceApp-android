package net.paceapp.core.enums

import androidx.annotation.StringRes
import net.paceapp.R

enum class DistanceUnits(
    @param:StringRes val titleRes: Int,
) {
    KMS(R.string.kms),
    MILES(R.string.miles);
}