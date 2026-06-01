package net.paceapp.core.domain.models

import net.paceapp.core.enums.DistanceUnits

data class DistanceModel(
    val value: Float,
    val unit: DistanceUnits = DistanceUnits.KMS
)

