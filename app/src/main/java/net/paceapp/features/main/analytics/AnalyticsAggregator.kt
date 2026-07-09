package net.paceapp.features.main.analytics

import net.paceapp.core.data.firestore.EventDocument
import net.paceapp.core.enums.AnalyticsMetricType
import net.paceapp.core.enums.AnalyticsPeriod
import net.paceapp.core.models.AnalyticsDataPoint
import net.paceapp.core.models.AnalyticsSummaryData
import java.util.Calendar
import java.util.Locale

// Client-side aggregation of completed events into the analytics UI models.
// Mirrors iOS AnalyticsModels+Firestore (dateRange / bucketRecords / averageValue)
// so both platforms produce identical charts and summary values.
object AnalyticsAggregator {

    // MARK: - Date range (matches iOS AnalyticsPeriod.dateRange)

    // [startMillis, endMillis] window for a period, ending "now".
    fun dateRange(period: AnalyticsPeriod, now: Long = System.currentTimeMillis()): Pair<Long, Long> {
        val cal = Calendar.getInstance().apply { timeInMillis = now }
        val start = Calendar.getInstance().apply { timeInMillis = now }
        when (period) {
            AnalyticsPeriod.DAY -> start.startOfDay()
            AnalyticsPeriod.WEEK -> {
                start.startOfDay()
                start.add(Calendar.DAY_OF_YEAR, -6)
            }
            AnalyticsPeriod.MONTH -> start.add(Calendar.MONTH, -1)
            AnalyticsPeriod.YEAR -> start.add(Calendar.YEAR, -1)
        }
        return start.timeInMillis to cal.timeInMillis
    }

    // MARK: - Bucketing (matches iOS AnalyticsPeriod.bucketRecords)

    // Ordered (label, records) buckets for the chart's X-axis.
    private fun bucketRecords(
        period: AnalyticsPeriod,
        records: List<EventDocument>,
        now: Long = System.currentTimeMillis(),
    ): List<Pair<String, List<EventDocument>>> {
        return when (period) {
            AnalyticsPeriod.DAY -> {
                val labels = listOf("12a", "3a", "6a", "9a", "12p", "3p", "6p", "9p")
                val slots = listOf(0, 3, 6, 9, 12, 15, 18, 21)
                labels.zip(slots) { label, hour ->
                    val group = records.filter {
                        val h = it.completedHour() ?: return@filter false
                        h in hour until hour + 3
                    }
                    label to group
                }
            }

            AnalyticsPeriod.WEEK -> {
                // Last 7 days ending today; labels derived from each column's real date.
                val today = Calendar.getInstance().apply { timeInMillis = now; startOfDay() }
                (0 until 7).map { offset ->
                    val day = (today.clone() as Calendar).apply { add(Calendar.DAY_OF_YEAR, offset - 6) }
                    val label = weekdayLabel(day)
                    val group = records.filter { it.isSameDay(day) }
                    label to group
                }
            }

            AnalyticsPeriod.MONTH -> {
                // Four rolling 7-day windows ending now (avoids year-boundary week bugs).
                listOf("W1", "W2", "W3", "W4").mapIndexed { index, label ->
                    val weeksBack = 3 - index
                    val upper = (Calendar.getInstance().apply { timeInMillis = now })
                        .apply { add(Calendar.DAY_OF_YEAR, -7 * weeksBack) }.timeInMillis
                    val lower = (Calendar.getInstance().apply { timeInMillis = upper })
                        .apply { add(Calendar.DAY_OF_YEAR, -7) }.timeInMillis
                    val group = records.filter {
                        val t = it.completedAt?.toDate()?.time ?: return@filter false
                        t > lower && t <= upper
                    }
                    label to group
                }
            }

            AnalyticsPeriod.YEAR -> {
                val months = listOf(
                    "Jan", "Feb", "Mar", "Apr", "May", "Jun",
                    "Jul", "Aug", "Sep", "Oct", "Nov", "Dec",
                )
                months.mapIndexed { index, label ->
                    val group = records.filter { it.completedMonth() == index }
                    label to group
                }
            }
        }
    }

    // MARK: - Metric averages (matches iOS AnalyticsMetricType.averageValue)

    private fun averageValue(metric: AnalyticsMetricType, records: List<EventDocument>): Double {
        if (records.isEmpty()) return 0.0
        return when (metric) {
            AnalyticsMetricType.PACE ->
                records.map { (it.avgPaceSeconds ?: 0).toDouble() }.average()
            AnalyticsMetricType.HEART_RATE ->
                records.map { (it.avgHeartRate ?: 0).toDouble() }.average()
            AnalyticsMetricType.PERCENTAGE ->
                records.map { it.effortPercentage ?: 0.0 }.average()
            // Android-only metric; averaged from elevationGain like the others.
            AnalyticsMetricType.ELEVATION ->
                records.map { it.elevationGain ?: 0.0 }.average()
        }
    }

    // Chart points for one metric over the period's buckets.
    fun dataPoints(
        period: AnalyticsPeriod,
        metric: AnalyticsMetricType,
        records: List<EventDocument>,
        now: Long = System.currentTimeMillis(),
    ): List<AnalyticsDataPoint> =
        bucketRecords(period, records, now).map { (label, group) ->
            AnalyticsDataPoint(label = label, value = averageValue(metric, group))
        }

    // MARK: - Summary cards

    // Per-metric summary cards for the detail screen (mirrors iOS buildSummaryCards).
    fun summaryList(
        metric: AnalyticsMetricType,
        period: AnalyticsPeriod,
        records: List<EventDocument>,
        now: Long = System.currentTimeMillis(),
    ): List<AnalyticsSummaryData> {
        val points = dataPoints(period, metric, records, now)
        return when (metric) {
            AnalyticsMetricType.PACE -> {
                val paces = records.mapNotNull { it.avgPaceSeconds }
                val avg = if (paces.isEmpty()) 0 else paces.average().toInt()
                // Best = fastest = smallest pace; ignore 0s so a missing value can't win.
                val best = paces.filter { it > 0 }.minOrNull() ?: 0
                listOf(
                    AnalyticsSummaryData(
                        title = "Avg Pace", value = formatPace(avg), unit = "min/mi",
                        type = metric, dataPoints = points,
                    ),
                    AnalyticsSummaryData(
                        title = "Best Pace", value = formatPace(best), unit = "min/mi",
                        type = metric, dataPoints = points,
                    ),
                )
            }

            AnalyticsMetricType.HEART_RATE -> {
                val hrs = records.mapNotNull { it.avgHeartRate }
                val avg = if (hrs.isEmpty()) 0 else hrs.average().toInt()
                val max = hrs.maxOrNull() ?: 0
                listOf(
                    AnalyticsSummaryData(
                        title = "Avg Heart Rate", value = "$avg", unit = "bpm",
                        type = metric, dataPoints = points,
                    ),
                    AnalyticsSummaryData(
                        title = "Max Heart Rate", value = "$max", unit = "bpm",
                        type = metric, dataPoints = points,
                    ),
                )
            }

            AnalyticsMetricType.PERCENTAGE -> {
                val efforts = records.mapNotNull { it.effortPercentage }
                val avg = if (efforts.isEmpty()) 0 else efforts.average().toInt()
                val peak = (efforts.maxOrNull() ?: 0.0).toInt()
                listOf(
                    AnalyticsSummaryData(
                        // Matches iOS AnalyticsMetricType.percentage ("Avg Efforts")
                        title = "Avg Efforts", value = "$avg", unit = "%",
                        type = metric, dataPoints = points,
                    ),
                    AnalyticsSummaryData(
                        title = "Peak Effort", value = "$peak", unit = "%",
                        type = metric, dataPoints = points,
                    ),
                )
            }

            AnalyticsMetricType.ELEVATION -> {
                // Android-only metric — no iOS counterpart; totals derived from records.
                val totalElevation = records.sumOf { it.elevationGain ?: 0.0 }
                val totalDistance = records.sumOf { it.distanceValue }
                listOf(
                    AnalyticsSummaryData(
                        title = "Overall ELEVATION Climbed", value = formatWhole(totalElevation),
                        unit = "ft", type = metric, dataPoints = points,
                    ),
                    AnalyticsSummaryData(
                        title = "Total Distance Covered", value = formatWhole(totalDistance),
                        unit = "mi.", type = metric,
                        dataPoints = dataPoints(period, AnalyticsMetricType.PACE, records, now),
                    ),
                )
            }
        }
    }

    // First summary card per metric, for the main list (one row per metric type).
    fun analyticsList(
        period: AnalyticsPeriod,
        records: List<EventDocument>,
        now: Long = System.currentTimeMillis(),
    ): List<AnalyticsSummaryData> =
        AnalyticsMetricType.entries.map { summaryList(it, period, records, now).first() }

    // MARK: - Formatting / helpers

    private fun formatPace(totalSeconds: Int): String =
        String.format(Locale.US, "%d:%02d", totalSeconds / 60, totalSeconds % 60)

    private fun formatWhole(value: Double): String =
        String.format(Locale.US, "%,d", value.toInt())

    private fun weekdayLabel(cal: Calendar): String {
        val fmt = java.text.SimpleDateFormat("EEE", Locale.US)
        return fmt.format(cal.time)
    }

    private fun Calendar.startOfDay() {
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }

    private fun EventDocument.completedHour(): Int? {
        val date = completedAt?.toDate() ?: return null
        return Calendar.getInstance().apply { time = date }.get(Calendar.HOUR_OF_DAY)
    }

    private fun EventDocument.completedMonth(): Int? {
        val date = completedAt?.toDate() ?: return null
        return Calendar.getInstance().apply { time = date }.get(Calendar.MONTH)
    }

    private fun EventDocument.isSameDay(day: Calendar): Boolean {
        val date = completedAt?.toDate() ?: return false
        val c = Calendar.getInstance().apply { time = date }
        return c.get(Calendar.YEAR) == day.get(Calendar.YEAR) &&
            c.get(Calendar.DAY_OF_YEAR) == day.get(Calendar.DAY_OF_YEAR)
    }
}
