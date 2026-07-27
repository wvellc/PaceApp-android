package net.paceapp.features.main.eventdetails.models

import net.paceapp.core.domain.models.DistanceModel

data class SegmentUiModel(
    val id: Int,
    val distance: DistanceModel = DistanceModel(0f),
    val durationInSeconds: Long = 0,
    // Completed (has an actual time) vs awaiting — drives the status pill (mirrors iOS).
    val isCompleted: Boolean = false,
)