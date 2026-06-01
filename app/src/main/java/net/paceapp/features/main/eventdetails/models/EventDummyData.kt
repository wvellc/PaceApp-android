package net.paceapp.features.main.eventdetails.models

import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.PolyUtil
import com.wvelabs.core_ui.utils.DateTimeHelper
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.atTime
import kotlinx.datetime.minus
import net.paceapp.core.domain.models.DistanceModel
import net.paceapp.core.enums.DistanceUnits
import net.paceapp.features.main.createevent.enums.EventType
import net.paceapp.features.main.createevent.extensions.labelRes
import kotlin.random.Random

object EventDummyData {

    // Exact coordinates for a 3-mile loop around Lady Bird Lake in Downtown Austin, TX
    private val mockCoordinates = listOf(
        CoordinateUiModel(30.2746, -97.7404),
        CoordinateUiModel(30.2634, -97.7443),
        CoordinateUiModel(30.2671, -97.7549),
        CoordinateUiModel(30.2745, -97.7515),
        CoordinateUiModel(30.2746, -97.7404)
    )

    val encodedPolyline = PolyUtil.encode(
        mockCoordinates.map {
            LatLng(it.latitude, it.longitude)
        }
    )

    fun getMockEvent(defaultUnit: DistanceUnits): EventDetailsUiModel {
        // Generate a random variance between -120s (2 mins fast) and +120s (2 mins slow)
        val randomVariance = Random.nextLong(-120, 120)
        val baseTargetTime = 1500L
        val actualTime = baseTargetTime + randomVariance

        // Generate 3 to 6 random intervals
        val randomIntervalCount = Random.nextInt(3, 7)
        val generatedIntervals = (1..randomIntervalCount).map { index ->
            IntervalUiModel(
                id = index,
                durationInSeconds = Random.nextLong(150L, 240L)
            )
        }

        return EventDetailsUiModel(
            id = "evt_${Random.nextInt(1000, 9999)}",
            title = "Lady Bird Lake Run", // Updated to Austin
            location = "Austin, TX",       // Updated to Austin
            dateTime = DateTimeHelper.now().date.minus(Random.nextInt(1, 5), DateTimeUnit.DAY)
                .atTime(6, 30, 0), // Early morning Austin run
            eventTypeRes = EventType.Run.labelRes,

            isAheadOfTime = Random.nextBoolean(),
            // Randomize Performance & Heart Rate
            performancePercentage = Random.nextInt(90, 115),
            averageHeartRateBpm = Random.nextInt(140, 175),

            targetDistance = DistanceModel(3.0f, defaultUnit),
            completedDistance = DistanceModel(3.0f, defaultUnit),

            // Dynamic Time Stats
            finishTimeGoalInSeconds = baseTargetTime,
            totalTimeTakenInSeconds = actualTime,
            timeVarianceInSeconds = randomVariance,

            lookBackIntervals = 1,
            encodedPolyline = encodedPolyline,
            coordinates = mockCoordinates,

            // Inject the randomly generated list
            intervals = generatedIntervals,

            segments = listOf(
                SegmentUiModel(
                    id = 1,
                    distance = DistanceModel(1.0f, defaultUnit),
                    durationInSeconds = 500L
                ),
                SegmentUiModel(
                    id = 2,
                    distance = DistanceModel(1.0f, defaultUnit),
                    durationInSeconds = 510L
                ),
                SegmentUiModel(
                    id = 3,
                    distance = DistanceModel(1.0f, defaultUnit),
                    durationInSeconds = 490L
                ),
                SegmentUiModel(
                    id = 4,
                    distance = DistanceModel(1.0f, defaultUnit),
                    durationInSeconds = 490L
                )
            )
        )
    }
}