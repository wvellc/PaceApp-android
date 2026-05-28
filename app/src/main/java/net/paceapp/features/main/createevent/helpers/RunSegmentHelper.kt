package net.paceapp.features.main.createevent.helpers

import com.wvelabs.core_ui.resources.ResourceProvider
import com.wvelabs.core_ui.utils.DateTimeHelper
import net.paceapp.R
import net.paceapp.core.domain.models.DistanceModel
import net.paceapp.features.main.createevent.models.RunSegment
import java.math.BigDecimal
import java.math.RoundingMode
import javax.inject.Inject
import kotlin.time.Duration.Companion.seconds

private const val MIN_DISTANCE = 0.1f
private const val MIN_DURATION_SECONDS = 1L

class RunSegmentHelper @Inject constructor(
    private val resourceProvider: ResourceProvider
) {

    private fun Float.round(decimals: Int = 2): Float {
        return BigDecimal(this.toDouble()).setScale(decimals, RoundingMode.HALF_UP).toFloat()
    }

    fun generateEqualSegments(
        totalDistance: Float, totalDurationSeconds: Long, segmentCount: Int
    ): List<RunSegment> {
        if (segmentCount <= 0) return emptyList()

        val splitDist = (totalDistance / segmentCount).round()
        val splitDur = totalDurationSeconds / segmentCount

        return List(segmentCount) { index ->
            val isLast = index == segmentCount - 1
            RunSegment(
                id = index + 1,
                distance = if (isLast) (totalDistance - (splitDist * (segmentCount - 1))).round() else splitDist,
                durationInSeconds = if (isLast) totalDurationSeconds - (splitDur * (segmentCount - 1)) else splitDur
            )
        }
    }

    fun updateSegmentValues(
        segments: List<RunSegment>,
        segmentIndex: Int,
        totalDistance: DistanceModel,
        totalDurationSeconds: Long,
    ): Pair<List<RunSegment>, String?> {
        if (segments.isEmpty() || segmentIndex !in segments.indices) {
            return Pair(segments, resourceProvider.getString(R.string.error_invalid_segment_config))
        }

        val newSegments = segments.toMutableList()
        val updatedSegment = newSegments[segmentIndex].copy(
            distance = newSegments[segmentIndex].distance.round()
        )

        // Check Zero Values
        getZeroValueErrorResId(updatedSegment)?.let { errorResId ->
            return Pair(segments, resourceProvider.getString(errorResId))
        }

        // Compute Locked & Available Totals
        val lockedDist =
            newSegments.take(segmentIndex).sumOf { it.distance.toDouble() }.toFloat().round()
        val lockedDur = newSegments.take(segmentIndex).sumOf { it.durationInSeconds }

        val availableDist = (totalDistance.value - lockedDist).round()
        val availableDur = totalDurationSeconds - lockedDur

        val remainingCount = newSegments.size - 1 - segmentIndex
        val unitStr = resourceProvider.getString(totalDistance.unit.titleRes)
        val formattedTotalDur =
            DateTimeHelper.formatDuration(totalDurationSeconds.seconds, forceShowHours = true)

        // Last segment or other segment validation
        val error = if (remainingCount == 0) {
            validateLastSegment(
                updated = updatedSegment,
                availableDist = availableDist,
                availableDur = availableDur,
                lockedDist = lockedDist,
                lockedDur = lockedDur,
                totalDist = totalDistance.value,
                unit = unitStr,
                formattedTotalDur = formattedTotalDur
            )
        } else {
            validateOtherSegments(
                updated = updatedSegment,
                availableDist = availableDist,
                availableDur = availableDur,
                lockedDist = lockedDist,
                lockedDur = lockedDur,
                remainingCount = remainingCount,
                totalDist = totalDistance.value,
                unit = unitStr,
                formattedTotalDur = formattedTotalDur
            )
        }

        if (error != null) return Pair(segments, error)

        // Apply Updates & Distribute
        newSegments[segmentIndex] = updatedSegment

        if (remainingCount > 0) {
            distributeForward(
                segments = newSegments,
                startIndex = segmentIndex + 1,
                leftoverDist = (availableDist - updatedSegment.distance).round(),
                leftoverDur = availableDur - updatedSegment.durationInSeconds,
                remainingCount = remainingCount
            )
        }

        return Pair(newSegments, null)
    }

    // ======================================================================
    // PRIVATE HELPERS
    // ======================================================================

    private fun getZeroValueErrorResId(segment: RunSegment): Int? = when {
        segment.distance <= 0f -> R.string.error_segment_zero_distance
        segment.durationInSeconds <= 0L -> R.string.error_segment_zero_eta
        else -> null
    }

    private fun validateLastSegment(
        updated: RunSegment,
        availableDist: Float,
        availableDur: Long,
        lockedDist: Float,
        lockedDur: Long,
        totalDist: Float,
        unit: String,
        formattedTotalDur: String
    ): String? {
        if (updated.distance != availableDist) {
            val calculated = (lockedDist + updated.distance).round()
            val calcStr = "$calculated $unit"
            val totalStr = "$totalDist $unit"

            val errorResId = if (updated.distance < availableDist) {
                R.string.error_segment_dist_below
            } else {
                R.string.error_segment_dist_exceeds
            }
            return resourceProvider.getString(errorResId, calcStr, totalStr)
        }

        if (updated.durationInSeconds != availableDur) {
            val calculated = lockedDur + updated.durationInSeconds
            val formattedCalc = DateTimeHelper.formatDuration(calculated.seconds)

            val errorResId = if (updated.durationInSeconds < availableDur) {
                R.string.error_segment_eta_below
            } else {
                R.string.error_segment_eta_exceeds
            }
            return resourceProvider.getString(errorResId, formattedCalc, formattedTotalDur)
        }

        return null
    }

    private fun validateOtherSegments(
        updated: RunSegment,
        availableDist: Float,
        availableDur: Long,
        lockedDist: Float,
        lockedDur: Long,
        remainingCount: Int,
        totalDist: Float,
        unit: String,
        formattedTotalDur: String
    ): String? {
        val minFutureDist = (remainingCount * MIN_DISTANCE).round()
        val minFutureDur = remainingCount * MIN_DURATION_SECONDS

        if (updated.distance > (availableDist - minFutureDist).round()) {
            val attemptedDist = (lockedDist + updated.distance + minFutureDist).round()
            val attemptStr = "$attemptedDist $unit"
            val totalStr = "$totalDist $unit"
            return resourceProvider.getString(
                R.string.error_segment_dist_exceeds, attemptStr, totalStr
            )
        }

        if (updated.durationInSeconds > availableDur - minFutureDur) {
            val attemptedDur = lockedDur + updated.durationInSeconds + minFutureDur
            val formattedAttempt = DateTimeHelper.formatDuration(attemptedDur.seconds)
            return resourceProvider.getString(
                R.string.error_segment_eta_exceeds, formattedAttempt, formattedTotalDur
            )
        }

        return null
    }

    private fun distributeForward(
        segments: MutableList<RunSegment>,
        startIndex: Int,
        leftoverDist: Float,
        leftoverDur: Long,
        remainingCount: Int
    ) {
        val splitDist = (leftoverDist / remainingCount).round()
        val splitDur = leftoverDur / remainingCount

        for (i in startIndex until segments.size) {
            val isLast = i == segments.size - 1

            segments[i] = segments[i].copy(
                distance = if (isLast) (leftoverDist - (splitDist * (remainingCount - 1))).round() else splitDist,
                durationInSeconds = if (isLast) leftoverDur - (splitDur * (remainingCount - 1)) else splitDur
            )
        }
    }
}