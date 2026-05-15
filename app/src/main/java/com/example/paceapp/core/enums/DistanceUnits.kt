package com.example.paceapp.core.enums

import androidx.annotation.StringRes
import com.example.paceapp.R

enum class DistanceUnits(
    @param:StringRes val titleRes: Int,
) {
    KM(R.string.kms),
    MILE(R.string.miles);
}