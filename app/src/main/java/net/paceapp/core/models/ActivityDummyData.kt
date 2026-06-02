package net.paceapp.core.models

import kotlinx.datetime.LocalDateTime
import net.paceapp.core.domain.models.DistanceModel
import net.paceapp.core.enums.DistanceUnits

object ActivityDummyData {

    fun getDummyActivities(): List<ActivityModel> = listOf(
        ActivityModel(
            id = "1",
            title = "Thursday Run",
            location = "Central Park",
            date = LocalDateTime(2024, 1, 29, 8, 30),
            distance = DistanceModel(value = 5.0f, unit = DistanceUnits.MILES),
            goalTime = 2700L, // 45 minutes in seconds
            avgPace = "9:00 /mi",
            paceDifference = "+01:10",
            isAheadOfTime = false
        ),
        ActivityModel(
            id = "2",
            title = "Saturday Run",
            location = "Golden Gate Trail",
            date = LocalDateTime(2024, 1, 31, 7, 0),
            distance = DistanceModel(value = 15.0f, unit = DistanceUnits.MILES),
            goalTime = 3000L, // 50 minutes in seconds
            avgPace = "3:20 /mi",
            paceDifference = "-02:15",
            isAheadOfTime = true
        ),
        ActivityModel(
            id = "3",
            title = "Saturday Run",
            location = "Riverside Path",
            date = LocalDateTime(2024, 1, 31, 18, 15),
            distance = DistanceModel(value = 15.0f, unit = DistanceUnits.MILES),
            goalTime = 3000L,
            avgPace = "3:20 /mi",
            paceDifference = "-02:15",
            isAheadOfTime = true
        ),
        ActivityModel(
            id = "4",
            title = "Saturday Run",
            location = "Downtown Loop",
            date = LocalDateTime(2024, 1, 31, 19, 0),
            distance = DistanceModel(value = 15.0f, unit = DistanceUnits.MILES),
            goalTime = 3000L,
            avgPace = "3:20 /mi",
            paceDifference = "-02:15",
            isAheadOfTime = true
        ),
        ActivityModel(
            id = "5",
            title = "Monday Run",
            location = "Highland Track",
            date = LocalDateTime(2024, 2, 2, 6, 45),
            distance = DistanceModel(value = 3.1f, unit = DistanceUnits.MILES),
            goalTime = 1500L, // 25 minutes in seconds
            avgPace = "8:03 /mi",
            paceDifference = "-00:45",
            isAheadOfTime = true
        ),
        ActivityModel(
            id = "6",
            title = "Wednesday Run",
            location = "Ocean Boulevard",
            date = LocalDateTime(2024, 2, 4, 17, 30),
            distance = DistanceModel(value = 7.5f, unit = DistanceUnits.MILES),
            goalTime = 4500L, // 1 hour 15 mins in seconds
            avgPace = "10:00 /mi",
            paceDifference = "+00:30",
            isAheadOfTime = false
        )
    )
}