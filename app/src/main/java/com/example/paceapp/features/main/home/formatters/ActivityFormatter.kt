package com.example.paceapp.features.main.home.formatters

import com.wvelabs.core_ui.utils.AppDateFormat
import com.wvelabs.core_ui.utils.DateTimeHelper
import kotlinx.datetime.LocalDateTime
import javax.inject.Inject
import kotlin.time.Duration

class ActivityFormatter @Inject constructor() {

    fun formatDate(date: LocalDateTime): String {
        return DateTimeHelper.formatDateTime(
            date = date,
            toFormat = AppDateFormat.DATE_SHORT_DM,
            isUtc = false
        ).orEmpty()
    }

    fun formatGoalTime(duration: Duration): String {
        val hours = duration.inWholeHours
        val minutes = duration.inWholeMinutes % 60
        val seconds = duration.inWholeSeconds % 60

        return "%02d:%02d:%02d".format(
            hours,
            minutes,
            seconds
        )
    }

    fun formatDistance(distanceMiles: Double): String {
        return "%.2f mi".format(distanceMiles)
    }
}