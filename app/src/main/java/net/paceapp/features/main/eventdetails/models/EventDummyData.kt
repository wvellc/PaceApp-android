package net.paceapp.features.main.eventdetails.models

import com.wvelabs.core_ui.utils.DateTimeHelper
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.atTime
import kotlinx.datetime.plus
import net.paceapp.features.main.createevent.enums.EventType
import net.paceapp.features.main.createevent.extensions.labelRes

object EventDummyData {

    // A realistic loop around Hyde Park, London
    private val mockCoordinates = listOf(
        CoordinateUiModel(51.5074, -0.1278), // Start
        CoordinateUiModel(51.5085, -0.1250),
        CoordinateUiModel(51.5100, -0.1265),
        CoordinateUiModel(51.5090, -0.1290),
        CoordinateUiModel(51.5074, -0.1278)  // Finish (loop complete)
    )

    // A real encoded polyline that draws a path when passed to Google Static Maps API
    private const val ENCODED_POLYLINE =
        "gkyyH|pZ|@a@?c@?e@A_@?u@?m@?i@?_@?g@?_@?e@?e@?g@?g@?g@?e@?c@?e@?c@?e@?c@?a@?a@?c@?c@?a@?c@?c@?a@?"


    val mockEvent = EventDetailsUiModel(
        id = "evt_12345",
        title = "Morning 5K Loop",
        location = "Hyde Park, London",
        // Using kotlinx.datetime to generate a future date
        dateTime = DateTimeHelper.now().date.plus(2, DateTimeUnit.DAY).atTime(0, 0, 0),
        eventTypeRes = EventType.Run.labelRes,
        targetPace = "5'30\"",

        // Flattened Route Data
        totalDistance = 5.0f,
        distanceUnit = "km",
        encodedPolyline = ENCODED_POLYLINE,
        coordinates = mockCoordinates,
        segments = listOf(
            // 1km in 6 minutes (360 seconds)
            SegmentUiModel(id = 1, distance = 1.0f, durationInSeconds = 360L),
            // 3km in 15 minutes (900 seconds)
            SegmentUiModel(id = 2, distance = 3.0f, durationInSeconds = 900L),
            // 1km in 6.5 minutes (390 seconds)
            SegmentUiModel(id = 3, distance = 1.0f, durationInSeconds = 390L)
        )
    )
}