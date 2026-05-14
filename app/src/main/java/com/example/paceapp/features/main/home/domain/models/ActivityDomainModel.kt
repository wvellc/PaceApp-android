package com.example.paceapp.features.main.home.domain.models

import kotlinx.datetime.LocalDateTime
import kotlin.time.Duration

data class ActivityDomainModel(
    val id: String,
    val name: String,
    val date: LocalDateTime,
    val distanceMiles: Double,
    val location: String,
    val goalTime: Duration
)