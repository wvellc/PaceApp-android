package net.paceapp.features.main.history.domain

import kotlinx.serialization.Serializable

@Serializable
data class RunHistoryModel(
    val id: String,
    val title: String,
    val timestamp: Long,
    val distance: Double,
    val durationSeconds: Int,
    val avgPaceSeconds: Int,
    val paceDifferenceSeconds: Int
)