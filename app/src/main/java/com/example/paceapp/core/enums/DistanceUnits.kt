package com.example.paceapp.core.enums

import androidx.annotation.StringRes
import com.example.paceapp.R

enum class DistanceUnits(
    @param:StringRes val titleRes: Int,
) {
    KMS(R.string.kms),
    MILES(R.string.miles);
}