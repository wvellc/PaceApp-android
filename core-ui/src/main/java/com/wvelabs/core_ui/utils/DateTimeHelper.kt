package com.wvelabs.core_ui.utils

import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.number
import kotlinx.datetime.plus
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import kotlin.math.abs
import kotlin.time.Clock
import kotlin.time.Duration

enum class AppDateFormat {
    TIMESTAMP,
    TIME_12_HOUR,
    TIME_12_HOUR_SHORT,
    DATE_MDY,
    DATE_YMD,
    DATE_DMY,
    DATE_FULL_MDY,
    DATE_SHORT_DM,
    DATE_SHORT_D,
    DATE_SHORT_MMM,
    DATE_MMM,
    DATE_WITH_DAY
}

object DateTimeHelper {

    private val SYSTEM_TZ: TimeZone
        get() = TimeZone.currentSystemDefault()

    private val MIDNIGHT = LocalTime(0, 0)

    fun formatDuration(duration: Duration): String {
        return duration.toComponents { hours, minutes, seconds, _ ->
            val totalMinutes = hours * 60 + minutes
            val sign = if (duration.isNegative()) "-" else ""
            "$sign${abs(totalMinutes).toString().padStart(2, '0')}:" +
                    abs(seconds).toString().padStart(2, '0')
        }
    }

    fun getDateTime(
        date: String?,
        format: AppDateFormat = AppDateFormat.TIMESTAMP,
        isUtc: Boolean = true
    ): LocalDateTime? {
        if (date.isNullOrBlank()) return null

        val parsed = parseLocalDateTime(date.trim(), format) ?: return null

        return if (!isUtc) parsed
        else parsed.toInstant(TimeZone.UTC)
            .toLocalDateTime(SYSTEM_TZ)
    }

    fun changeDateFormat(
        date: String?,
        from: AppDateFormat = AppDateFormat.TIMESTAMP,
        to: AppDateFormat = AppDateFormat.DATE_MDY,
        isUtc: Boolean = true
    ): String? {
        val localDateTime = getDateTime(date, from, isUtc) ?: return null
        return formatDateTime(localDateTime, to, false)
    }

    fun formatDateTime(
        date: LocalDateTime?,
        toFormat: AppDateFormat = AppDateFormat.TIMESTAMP,
        isUtc: Boolean = true
    ): String? {
        if (date == null) return null

        val formattedDate = if (isUtc) {
            date.toInstant(SYSTEM_TZ)
                .toLocalDateTime(TimeZone.UTC)
        } else date

        return formatLocalDateTime(formattedDate, toFormat)
    }

    fun getRelativeTime(dateTime: LocalDateTime?): String {
        if (dateTime == null) return ""

        val diffSeconds = (
                Clock.System.now().toEpochMilliseconds() -
                        dateTime.toInstant(SYSTEM_TZ).toEpochMilliseconds()
                ) / 1000

        return when {
            diffSeconds < 60 -> "Just now"
            diffSeconds < 3600 -> "${diffSeconds / 60} min ago"
            diffSeconds < 86400 -> "${diffSeconds / 3600} hours ago"
            diffSeconds < 604800 -> "${diffSeconds / 86400} days ago"
            diffSeconds < 2592000 -> "${diffSeconds / 604800} weeks ago"
            diffSeconds < 31536000 -> "${diffSeconds / 2592000} months ago"
            else -> "${diffSeconds / 31536000} years ago"
        }
    }

    fun isSameDay(first: LocalDateTime?, second: LocalDateTime?): Boolean {
        return first?.date == second?.date
    }

    fun isToday(date: LocalDateTime?): Boolean {
        if (date == null) return false
        val today = Clock.System.now().toLocalDateTime(SYSTEM_TZ)
        return isSameDay(date, today)
    }

    fun isTomorrow(date: LocalDateTime?): Boolean {
        if (date == null) return false
        val tomorrow = Clock.System.now()
            .plus(1, DateTimeUnit.DAY, SYSTEM_TZ)
            .toLocalDateTime(SYSTEM_TZ)
        return isSameDay(date, tomorrow)
    }

    private fun parseLocalDateTime(
        value: String,
        format: AppDateFormat
    ): LocalDateTime? {
        return when (format) {
            AppDateFormat.TIMESTAMP -> {
                val parts = value.split('T', limit = 2)
                val date = parts.getOrNull(0)?.parseIsoDate() ?: return null
                val time = parts.getOrNull(1)?.parseTime24() ?: return null
                LocalDateTime(date, time)
            }

            AppDateFormat.TIME_12_HOUR,
            AppDateFormat.TIME_12_HOUR_SHORT -> {
                value.parseTime12()?.let {
                    LocalDateTime(LocalDate(1970, 1, 1), it)
                }
            }

            AppDateFormat.DATE_MDY -> {
                value.parseDateWithSeparator('/') { m, d, y ->
                    LocalDate(y, m, d)
                }?.atStartOfDay()
            }

            AppDateFormat.DATE_YMD -> {
                value.parseIsoDate()?.atStartOfDay()
            }

            AppDateFormat.DATE_DMY -> {
                value.parseDayMonthYear()?.atStartOfDay()
            }

            AppDateFormat.DATE_FULL_MDY -> {
                value.parseMonthDayYear()?.atStartOfDay()
            }

            AppDateFormat.DATE_SHORT_DM -> {
                value.parseDayMonth(1970)?.atStartOfDay()
            }

            AppDateFormat.DATE_SHORT_D -> {
                value.toIntOrNull()?.let {
                    LocalDate(1970, 1, it).atStartOfDay()
                }
            }

            AppDateFormat.DATE_SHORT_MMM,
            AppDateFormat.DATE_MMM -> {
                value.parseMonthName()?.let {
                    LocalDate(1970, it, 1).atStartOfDay()
                }
            }

            AppDateFormat.DATE_WITH_DAY -> {
                value.substringAfter(' ', "")
                    .parseDayMonth(1970)
                    ?.atStartOfDay()
            }
        }
    }

    private fun formatLocalDateTime(
        dateTime: LocalDateTime,
        format: AppDateFormat
    ): String {
        val date = dateTime.date
        val time = dateTime.time

        return when (format) {
            AppDateFormat.TIMESTAMP ->
                "${date.year.pad(4)}-${date.month.number.pad()}-${date.day.pad()}" +
                        "T${time.hour.pad()}:${time.minute.pad()}:${time.second.pad()}"

            AppDateFormat.TIME_12_HOUR -> time.to12hours()
            AppDateFormat.TIME_12_HOUR_SHORT -> time.to12hours().replace(" ", "")
            AppDateFormat.DATE_MDY ->
                "${date.month.number.pad()}/${date.day.pad()}/${date.year.pad(4)}"

            AppDateFormat.DATE_YMD ->
                "${date.year.pad(4)}-${date.month.number.pad()}-${date.day.pad()}"

            AppDateFormat.DATE_DMY ->
                "${date.day.pad()} ${date.monthShortName}, ${date.year.pad(4)}"

            AppDateFormat.DATE_FULL_MDY ->
                "${date.monthFullName} ${date.day.pad()}, ${date.year.pad(4)}"

            AppDateFormat.DATE_SHORT_DM ->
                "${date.day.pad()} ${date.monthShortName}"

            AppDateFormat.DATE_SHORT_D -> date.day.pad()

            AppDateFormat.DATE_SHORT_MMM,
            AppDateFormat.DATE_MMM -> date.monthShortName

            AppDateFormat.DATE_WITH_DAY ->
                "${date.dayOfWeek.shortName} ${date.day.pad()} ${date.monthShortName}"
        }
    }

    private fun LocalDate.atStartOfDay() =
        LocalDateTime(this, MIDNIGHT)
}

fun LocalTime.to24hours(): String =
    "${hour.toString().padStart(2, '0')}:${minute.toString().padStart(2, '0')}"

fun LocalTime.to12hours(): String {
    val hourOfPeriod = if (hour % 12 == 0) 12 else hour % 12
    val period = if (hour < 12) "AM" else "PM"
    return "$hourOfPeriod:${minute.toString().padStart(2, '0')} $period"
}

private val monthShortNames = listOf(
    "Jan", "Feb", "Mar", "Apr", "May", "Jun",
    "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"
)

private val monthFullNames = listOf(
    "January", "February", "March", "April", "May", "June",
    "July", "August", "September", "October", "November", "December"
)

private val monthMap = buildMap {
    monthShortNames.forEachIndexed { i, s -> put(s.lowercase(), i + 1) }
    monthFullNames.forEachIndexed { i, s -> put(s.lowercase(), i + 1) }
}

private val LocalDate.monthShortName: String
    get() = monthShortNames[month.number - 1]

private val LocalDate.monthFullName: String
    get() = monthFullNames[month.number - 1]

private val DayOfWeek.shortName: String
    get() = name.take(3).lowercase().replaceFirstChar { it.uppercase() }

private fun Int.pad(length: Int = 2) =
    toString().padStart(length, '0')

private fun String.parseMonthName(): Int? =
    monthMap[trim().trimEnd(',').lowercase()]

private fun String.parseIsoDate(): LocalDate? =
    parseDateWithSeparator('-') { y, m, d ->
        LocalDate(y, m, d)
    }

private fun String.parseDayMonthYear(): LocalDate? {
    val parts = replace(",", "").split(' ')
        .filter { it.isNotBlank() }

    if (parts.size != 3) return null

    val year = parts[2].toIntOrNull() ?: return null
    val month = parts[1].parseMonthName() ?: return null
    val day = parts[0].toIntOrNull() ?: return null

    return LocalDate(year, month, day)
}

private fun String.parseMonthDayYear(): LocalDate? {
    val parts = replace(",", "").split(' ')
        .filter { it.isNotBlank() }

    if (parts.size != 3) return null

    val year = parts[2].toIntOrNull() ?: return null
    val month = parts[0].parseMonthName() ?: return null
    val day = parts[1].toIntOrNull() ?: return null

    return LocalDate(year, month, day)
}

private fun String.parseDayMonth(defaultYear: Int): LocalDate? {
    val parts = split(' ').filter { it.isNotBlank() }

    if (parts.size != 2) return null

    val day = parts[0].toIntOrNull() ?: return null
    val month = parts[1].parseMonthName() ?: return null

    return LocalDate(defaultYear, month, day)
}

private inline fun String.parseDateWithSeparator(
    separator: Char,
    createDate: (Int, Int, Int) -> LocalDate
): LocalDate? {
    val parts = split(separator)
    if (parts.size != 3) return null

    val first = parts[0].toIntOrNull() ?: return null
    val second = parts[1].toIntOrNull() ?: return null
    val third = parts[2].toIntOrNull() ?: return null

    return createDate(first, second, third)
}

private fun String.parseTime24(): LocalTime? {
    val parts = split(':')
    if (parts.size !in 2..3) return null

    val hour = parts[0].toIntOrNull() ?: return null
    val minute = parts[1].toIntOrNull() ?: return null
    val second = parts.getOrNull(2)?.toIntOrNull() ?: 0

    if (hour !in 0..23 || minute !in 0..59 || second !in 0..59) {
        return null
    }

    return LocalTime(hour, minute, second)
}

private fun String.parseTime12(): LocalTime? {
    val cleaned = trim().uppercase()

    val marker = when {
        cleaned.endsWith("AM") -> "AM"
        cleaned.endsWith("PM") -> "PM"
        else -> return null
    }

    val time = cleaned.removeSuffix(marker).trim()
    val parts = time.split(':')

    if (parts.size != 2) return null

    val hourOfPeriod = parts[0].toIntOrNull() ?: return null
    val minute = parts[1].toIntOrNull() ?: return null

    val hour = when {
        hourOfPeriod !in 1..12 -> return null
        marker == "AM" && hourOfPeriod == 12 -> 0
        marker == "AM" -> hourOfPeriod
        hourOfPeriod == 12 -> 12
        else -> hourOfPeriod + 12
    }

    return LocalTime(hour, minute)
}
