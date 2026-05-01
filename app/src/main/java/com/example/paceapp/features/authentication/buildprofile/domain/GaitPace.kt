package com.example.paceapp.features.authentication.buildprofile.domain

data class GaitPace(
    val value: Float,
    val unit: GaitUnit = GaitUnit.METERS // Default unit
)

