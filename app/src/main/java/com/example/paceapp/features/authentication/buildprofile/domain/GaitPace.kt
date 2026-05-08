package com.example.paceapp.features.authentication.buildprofile.domain

import kotlinx.serialization.Serializable

@Serializable
data class GaitPace(
    val value: Float,
    val unit: GaitUnit = GaitUnit.METERS // Default unit
)

