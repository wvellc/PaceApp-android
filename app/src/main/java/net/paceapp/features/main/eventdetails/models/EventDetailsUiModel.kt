package net.paceapp.features.main.eventdetails.models

import androidx.annotation.StringRes
import kotlinx.datetime.LocalDateTime
import net.paceapp.core.domain.models.DistanceModel


data class EventDetailsUiModel(
    val id: String,
    val title: String,
    val location: String,
    val dateTime: LocalDateTime,

    @param:StringRes val eventTypeRes: Int,

    // Whether the event has actuals (completed) vs upcoming/active. Gates the
    // completed-only stats/sections so an active event shows only its planned data
    // (mirrors iOS isCompletedEvent).
    val isCompleted: Boolean,

    // --- Top Level Stats ---
    val performancePercentage: Int?, // 110
    val isAheadOfTime: Boolean, // 110

    // --- Distance Stats ---
    val targetDistance: DistanceModel, // 1.00
    val completedDistance: DistanceModel, // 1.3

    // --- Time Stats (in seconds for UI formatting) ---
    val finishTimeGoalInSeconds: Long, // 00:06:00 -> 360L
    val totalTimeTakenInSeconds: Long, // 00:03:51 -> 231L
    val timeVarianceInSeconds: Long, // -00:02:09 -> -129L

    // --- Extra Analytics ---
    val lookBackIntervals: Int, // 1
    val averageHeartRateBpm: Int?, // 157

    // --- Route & Map Data ---
    val encodedPolyline: String,
    val coordinates: List<CoordinateUiModel>,

    // --- Lists ---
    val intervals: List<IntervalUiModel>,
    val segments: List<SegmentUiModel>
)


