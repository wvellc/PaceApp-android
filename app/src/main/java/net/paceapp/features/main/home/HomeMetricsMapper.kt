package net.paceapp.features.main.home

import com.wvelabs.core_ui.utils.DateTimeHelper
import net.paceapp.R
import net.paceapp.core.data.firestore.EventDocument
import net.paceapp.core.data.firestore.EventDocumentMapper
import net.paceapp.features.main.home.models.WatchMetric
import java.util.Locale
import kotlin.time.Duration.Companion.seconds

// Maps the latest completed event into the five Home header capsules, mirroring iOS
// HomeViewModel.makeMetrics. Capsule 2 (FLASH_SLOT_INDEX) has two faces — total distance
// and actual finish time — that HomeViewModel alternates on a 2.5s flash cadence.
object HomeMetricsMapper {

    // Index of the distance⇄finish capsule the flash loop swaps in place.
    const val FLASH_SLOT_INDEX = 1

    fun metrics(doc: EventDocument, showDistanceFace: Boolean): List<WatchMetric> {
        val paceUnit = if (doc.measure == "Kilometers") "min/km" else "min/mile"
        return listOf(
            // 1 — Average heart rate overall.
            WatchMetric(
                id = "avgHeartRate",
                iconRes = R.drawable.ic_metrics_heart_rate,
                value = (doc.avgHeartRate ?: 0).toString(),
                unit = "bpm",
                title = "Average Heart Rate",
                description = "Your average heart rate recorded across the whole run.",
            ),
            // 2 — Total distance ⇄ actual finish time (flashes back and forth).
            face(doc, showDistanceFace),
            // 3 — Amount of time faster or slower than goal.
            WatchMetric(
                id = "timeVariance",
                iconRes = R.drawable.ic_metrics_goal_time,
                value = doc.timeVarianceSeconds?.let { EventDocumentMapper.formatSignedVariance(it) }
                    ?: "00:00",
                unit = "m /sec",
                title = "Time Variance",
                description = "How much faster or slower you finished compared to your goal time.",
            ),
            // 4 — Average pace for the event.
            WatchMetric(
                id = "avgPace",
                iconRes = R.drawable.ic_metrics_remaining_time,
                value = formatPace(doc.avgPaceSeconds),
                unit = paceUnit,
                title = "Average Pace",
                description = "Your average pace for this run.",
            ),
            // 5 — Goal time originally entered when creating the event.
            WatchMetric(
                id = "goalTime",
                iconRes = R.drawable.ic_metrics_pace,
                value = DateTimeHelper.formatDuration(doc.goalTimeSeconds.seconds),
                unit = "h:m:s",
                title = "Goal Time",
                description = "The finish-time goal you set when you created this event.",
            ),
        )
    }

    // The current face of the flashing capsule.
    fun face(doc: EventDocument, showDistanceFace: Boolean): WatchMetric =
        if (showDistanceFace) distanceFace(doc) else finishTimeFace(doc)

    // Distance face — actual distance covered (with km/mi unit), falling back to the
    // planned distance rendered inline with its unit when no actuals exist yet.
    private fun distanceFace(doc: EventDocument): WatchMetric {
        val unitWord = if (doc.measure == "Kilometers") "km" else "mi"
        val actual = doc.actualDistance
        val (value, unit) = if (actual != null) {
            String.format(Locale.US, "%.2f", actual) to unitWord
        } else {
            String.format(Locale.US, "%.2f %s", doc.distanceValue, unitWord) to ""
        }
        return WatchMetric(
            id = "distanceFinishFlash",
            iconRes = R.drawable.ic_metrics_overall_time,
            value = value,
            unit = unit,
            title = "Total Distance",
            description = "The total distance you covered in this run.",
        )
    }

    // Finish-time face — the actual completion time (HH:MM:SS).
    private fun finishTimeFace(doc: EventDocument): WatchMetric {
        val value = doc.actualTimeSeconds?.let { DateTimeHelper.formatDuration(it.seconds) }
            ?: "00:00:00"
        return WatchMetric(
            id = "distanceFinishFlash",
            iconRes = R.drawable.ic_metrics_overall_time,
            value = value,
            unit = "time",
            title = "Finish Time",
            description = "Your actual finishing time for this run.",
        )
    }

    // Pace seconds → "MM:SS"; "00:00" when unavailable (mirrors iOS avgPaceFormatted).
    private fun formatPace(seconds: Int?): String {
        if (seconds == null || seconds <= 0) return "00:00"
        return String.format(Locale.US, "%02d:%02d", seconds / 60, seconds % 60)
    }
}
