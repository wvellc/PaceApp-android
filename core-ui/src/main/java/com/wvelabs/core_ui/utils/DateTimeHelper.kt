package com.wvelabs.core_ui.utils

import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDate.Companion.Format
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.format
import kotlinx.datetime.format.DateTimeFormat
import kotlinx.datetime.format.MonthNames
import kotlinx.datetime.format.char
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object DateTimeHelper {

    private const val DEFAULT_FORMAT = "dd MMM, yyyy"


    fun parseMillisToDate(millis: Long?, toFormat: String = DEFAULT_FORMAT): String {
        if (millis == null) return ""
        val formatter = SimpleDateFormat(toFormat, Locale.getDefault())
        return formatter.format(Date(millis))
    }

    fun formatDateToMillis(date: String?, fromFor: String = DEFAULT_FORMAT): Long? {
        return try {
            if (date.isNullOrBlank()) return null
            val dateFormat = SimpleDateFormat(fromFor, Locale.getDefault())
            dateFormat.parse(date)?.time
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }

    }


    fun parseToAmPMTime(time: String): String? {
        return try {
            LocalTime.parse(time).format(LocalTime.HH_MM_A)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }

    }
}

val DateTimeUnit.title: String
    get() = when (this) {
        DateTimeUnit.YEAR -> "Years"
        DateTimeUnit.MONTH -> "Months"
        DateTimeUnit.WEEK -> "Weeks"
        DateTimeUnit.DAY -> "Days"
        DateTimeUnit.HOUR -> "Hours"
        DateTimeUnit.MINUTE -> "Mins"
        DateTimeUnit.SECOND -> "Seconds"
        else -> toString()
    }

val LocalDate.Companion.YY_MM_DD: DateTimeFormat<LocalDate> by lazy {
    Format {
        year()
        char('-')
        monthNumber()
        char('-')
        dayOfMonth()
    }
}
val LocalDate.Companion.DD_MMM_YYYY: DateTimeFormat<LocalDate> by lazy {
    Format {
        dayOfMonth()
        char(' ')
        monthName(MonthNames.ENGLISH_ABBREVIATED)
        char(',')
        char(' ')
        year()
    }
}

val LocalTime.Companion.HH_MM_A by lazy {
    LocalTime.Format {
        amPmHour()
        char(':')
        minute()
        char(' ')
        amPmMarker(am = "am", pm = "pm")
    }
}

fun LocalTime.Companion.parseOrNull(
    input: String,
    format: DateTimeFormat<LocalTime> = LocalTime.Formats.ISO
): LocalTime? {
    return try {
        parse(input, format)
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

fun LocalDate.Companion.parseOrNull(
    input: String?,
    format: DateTimeFormat<LocalDate> = LocalDate.Formats.ISO
): LocalDate? {
    if (input == null) return null;
    return try {
        parse(input, format)
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

fun String.parseOrNull(
    format: DateTimeFormat<LocalDateTime> = LocalDateTime.Formats.ISO
): LocalDateTime? = try {
    LocalDateTime.parse(this, format)
} catch (e: Exception) {
    e.printStackTrace()
    null
}