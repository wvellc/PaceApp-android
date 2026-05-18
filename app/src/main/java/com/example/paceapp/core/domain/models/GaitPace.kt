package com.example.paceapp.core.domain.models

import kotlinx.serialization.Serializable

@Serializable
data class GaitPace(
    val value: Float,
    val unit: GaitUnit = GaitUnit.METERS // Default unit
)

