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
    val mockCoordinates = listOf(
        CoordinateUiModel(40.666349, -74.214964),
        CoordinateUiModel(40.666351, -74.214964),
        CoordinateUiModel(40.666589, -74.213397),
        CoordinateUiModel(40.667108, -74.210215),
        CoordinateUiModel(40.667117, -74.210118),
        CoordinateUiModel(40.667105, -74.210012),
        CoordinateUiModel(40.667080, -74.209892),
        CoordinateUiModel(40.667108, -74.209878),
        CoordinateUiModel(40.667080, -74.209892),
        CoordinateUiModel(40.666565, -74.208271),
        CoordinateUiModel(40.666190, -74.207139),
        CoordinateUiModel(40.665875, -74.206130),
        CoordinateUiModel(40.665519, -74.205041),
        CoordinateUiModel(40.665062, -74.203670),
        CoordinateUiModel(40.662094, -74.205338),
        CoordinateUiModel(40.662277, -74.205923),
        CoordinateUiModel(40.662599, -74.206870),
        CoordinateUiModel(40.662651, -74.206839),
        CoordinateUiModel(40.662599, -74.206870),
        CoordinateUiModel(40.662753, -74.207334),
        CoordinateUiModel(40.663017, -74.208182),
        CoordinateUiModel(40.663376, -74.209234),
        CoordinateUiModel(40.663501, -74.209625),
        CoordinateUiModel(40.663889, -74.210708),
        CoordinateUiModel(40.663970, -74.210914),
        CoordinateUiModel(40.664047, -74.211082),
        CoordinateUiModel(40.664220, -74.211468),
        CoordinateUiModel(40.664183, -74.211498),
        CoordinateUiModel(40.664164, -74.211525),
        CoordinateUiModel(40.664175, -74.211538),
        CoordinateUiModel(40.664512, -74.212413),
        CoordinateUiModel(40.664583, -74.212379),
        CoordinateUiModel(40.664812, -74.212951),
        CoordinateUiModel(40.664901, -74.213219),
        CoordinateUiModel(40.665301, -74.214550),
        CoordinateUiModel(40.665328, -74.214768),
        CoordinateUiModel(40.665487, -74.214759),
        CoordinateUiModel(40.666346, -74.214997),
        CoordinateUiModel(40.666351, -74.214964)
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

            isCompleted = true,
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