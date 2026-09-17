package net.paceapp.core.models

import kotlinx.datetime.LocalDateTime
import net.paceapp.core.domain.models.DistanceModel

data class ActivityModel(
    val id: String,
    val title: String,
    val location: String,
    val date: LocalDateTime,
    val distance: DistanceModel,
    val goalTime: Long,
    val avgPace: String,
    val paceDifference: String,
    val isAheadOfTime: Boolean,
    // Canonical activity type ("Run"/"Walking"/"Cycling"/"Other") — drives the row icon.
    val activityType: String = "Run",
)
