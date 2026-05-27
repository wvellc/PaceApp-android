package net.paceapp.features.main.createevent.helpers

import com.wvelabs.core_ui.utils.DateTimeHelper
import net.paceapp.features.main.createevent.models.RunSegment
import javax.inject.Inject
import kotlin.time.Duration.Companion.seconds

class RunSegmentHelper @Inject constructor() {


    fun generateEqualSegments(
        totalDistance: Float,
        totalDurationSeconds: Long,
        segmentCount: Int
    ): List<RunSegment> { // Assuming SegmentModel is your data class
        if (segmentCount <= 0) return emptyList()

        val splitDistance = totalDistance / segmentCount
        val splitDuration = totalDurationSeconds / segmentCount

        // Generates identical segments distributed evenly
        return List(segmentCount) {
            RunSegment(
                id = it + 1,
                distance = splitDistance,
                durationInSeconds = splitDuration
            )
        }
    }

    fun updateSegmentValues(
        segments: List<RunSegment>,
        segmentIndex: Int,
        totalDistance: Float,
        totalDurationSeconds: Long,
    ): Pair<List<RunSegment>, String?> {
        if (segments.isEmpty() || segmentIndex !in segments.indices) {
            return Pair(segments, "Invalid segment configuration.")
        }

        val newSegments = segments.toMutableList()
        val updatedSegment = newSegments[segmentIndex]
        val calculatedDistance =
            newSegments.take(segmentIndex).sumOf { it.distance.toDouble() }.toFloat()
        val calculatedDuration =
            newSegments.take(segmentIndex).sumOf { it.durationInSeconds }

        val availableDistance = totalDistance - calculatedDistance
        val availableDuration = totalDurationSeconds - calculatedDuration
        if (updatedSegment.distance > availableDistance) {
            val attemptedTotalDistance = calculatedDistance + updatedSegment.distance
            return Pair(
                newSegments.toList(),
                "Combined segment distance ($attemptedTotalDistance) cannot exceed total ($totalDistance)"
            )
        }

        if (updatedSegment.durationInSeconds > availableDuration) {
            val attemptedTotalDuration = calculatedDuration + updatedSegment.durationInSeconds

            val formattedDuration = DateTimeHelper.formatDuration(totalDurationSeconds.seconds)
            val formattedAttemptDuration =
                DateTimeHelper.formatDuration(attemptedTotalDuration.seconds)
            return Pair(
                newSegments.toList(),
                "Combined segment ETA ($formattedAttemptDuration)  cannot exceed total goal ($formattedDuration)"
            )
        }

        // Divvy up the leftover balance equally to the REMAINING segments
        val remainingSegmentsCount = newSegments.size - 1 - segmentIndex

        if (remainingSegmentsCount > 0) {
            val leftoverDistance = availableDistance - updatedSegment.distance
            val leftoverDuration = availableDuration - updatedSegment.durationInSeconds

            val splitDistance = leftoverDistance / remainingSegmentsCount
            val splitDuration = leftoverDuration / remainingSegmentsCount

            for (i in (segmentIndex + 1) until newSegments.size) {
                newSegments[i] = newSegments[i].copy(
                    distance = splitDistance,
                    durationInSeconds = splitDuration
                )
            }
        }

        return Pair(newSegments.toList(), null)
    }
}