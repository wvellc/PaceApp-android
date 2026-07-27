package net.paceapp.features.main.eventdetails.mappers

import com.google.maps.android.PolyUtil
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import net.paceapp.core.data.firestore.EventDocument
import net.paceapp.core.data.firestore.EventStatusValue
import net.paceapp.core.domain.models.DistanceModel
import net.paceapp.core.enums.DistanceUnits
import net.paceapp.features.main.createevent.enums.EventType
import net.paceapp.features.main.createevent.extensions.labelRes
import net.paceapp.features.main.eventdetails.models.CoordinateUiModel
import net.paceapp.features.main.eventdetails.models.EventDetailsUiModel
import net.paceapp.features.main.eventdetails.models.IntervalUiModel
import net.paceapp.features.main.eventdetails.models.SegmentUiModel

// Maps a Firestore EventDocument to the Event Details UI model. Direct display
// mapping — no computed pace/analytics beyond what the document already carries.
// Mirrors iOS Event Details sourcing so both phones render identical screens.
object EventDetailsMapper {

    fun toUiModel(document: EventDocument): EventDetailsUiModel {
        val unit = if (document.measure == "Miles") DistanceUnits.MILES else DistanceUnits.KMS

        val dateTime: LocalDateTime = document.scheduledAt?.let { ts ->
            Instant.fromEpochMilliseconds(ts.toDate().time)
                .toLocalDateTime(TimeZone.currentSystemDefault())
        } ?: Instant.fromEpochMilliseconds(System.currentTimeMillis())
            .toLocalDateTime(TimeZone.currentSystemDefault())

        val coordinates = document.routePolyline
            ?.let { PolyUtil.decode(it) }
            ?.map { CoordinateUiModel(it.latitude, it.longitude) }
            .orEmpty()

        // Per-interval pace list from the document; index becomes the 1-based id.
        val intervals = document.paces.orEmpty().mapIndexed { index, pace ->
            IntervalUiModel(id = index + 1, durationInSeconds = pace)
        }

        // Embedded segments — duration prefers actual, falls back to the goal time.
        val segments = document.segments.orEmpty().map { seg ->
            SegmentUiModel(
                id = seg.index,
                distance = DistanceModel(seg.distance.toFloat(), unit),
                durationInSeconds = (seg.actualTimeSeconds ?: seg.goalTimeSeconds).toLong()
            )
        }

        return EventDetailsUiModel(
            id = document.id.toString(),
            title = document.name,
            location = document.location,
            dateTime = dateTime,
            eventTypeRes = eventTypeRes(document.activityType),

            isCompleted = document.status == EventStatusValue.COMPLETED,

            // effortPercentage (0..100) rounded; null when the event isn't completed.
            performancePercentage = document.effortPercentage?.let { Math.round(it).toInt() },
            isAheadOfTime = (document.timeVarianceSeconds ?: 0) < 0,

            targetDistance = DistanceModel(document.distanceValue.toFloat(), unit),
            completedDistance = DistanceModel(
                (document.actualDistance ?: document.distanceValue).toFloat(),
                unit
            ),

            finishTimeGoalInSeconds = document.goalTimeSeconds.toLong(),
            totalTimeTakenInSeconds = document.actualTimeSeconds?.toLong() ?: 0L,
            timeVarianceInSeconds = document.timeVarianceSeconds?.toLong() ?: 0L,

            lookBackIntervals = document.lookBackIntervals,
            averageHeartRateBpm = document.avgHeartRate,

            encodedPolyline = document.routePolyline ?: "",
            coordinates = coordinates,

            intervals = intervals,
            segments = segments
        )
    }

    // The route map is only meaningful with an actual trace (matches iOS hasRouteData).
    fun hasRouteData(coordinates: List<CoordinateUiModel>): Boolean = coordinates.size >= 2

    // activityType canonical rawValue → the create-event EventType used for the label.
    private fun eventTypeRes(activityType: String): Int = when (activityType) {
        "Walking" -> EventType.Walk.labelRes
        "Cycling" -> EventType.Cycle.labelRes
        "Other" -> EventType.Other.labelRes
        else -> EventType.Run.labelRes
    }
}
