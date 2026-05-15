package com.example.paceapp.core.models

import com.example.paceapp.core.enums.AnalyticsMetricType
import com.example.paceapp.core.enums.AnalyticsPeriod

object AnalyticsDummyData {

    fun getDataPoints(
        period: AnalyticsPeriod,
        metricType: AnalyticsMetricType
    ): List<AnalyticsDataPoint> {
        return when (period) {
            AnalyticsPeriod.DAY -> dayPoints(metricType)
            AnalyticsPeriod.WEEK -> weekPoints(metricType)
            AnalyticsPeriod.MONTH -> monthPoints(metricType)
            AnalyticsPeriod.YEAR -> yearPoints(metricType)
        }
    }

    private fun dayPoints(metric: AnalyticsMetricType): List<AnalyticsDataPoint> {
        val hours = listOf("12a", "3a", "6a", "9a", "12p", "3p", "6p", "9p")
        val values = when (metric) {
            AnalyticsMetricType.PACE -> listOf(10.0, 20.0, 15.0, 50.0, 80.0, 65.0, 90.0, 70.0)
            AnalyticsMetricType.HEART_RATE -> listOf(8.0, 12.0, 10.0, 35.0, 60.0, 55.0, 80.0, 60.0)
            AnalyticsMetricType.ELEVATION -> listOf(40.0, 45.0, 42.0, 55.0, 65.0, 70.0, 80.0, 75.0)
            AnalyticsMetricType.PERCENTAGE -> listOf(10.0, 18.0, 14.0, 45.0, 72.0, 60.0, 85.0, 65.0)
        }
        return hours.zip(values) { label, value -> AnalyticsDataPoint(label, value) }
    }

    private fun weekPoints(metric: AnalyticsMetricType): List<AnalyticsDataPoint> {
        val days = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
        val values = when (metric) {
            AnalyticsMetricType.PACE -> listOf(20.0, 45.0, 30.0, 70.0, 55.0, 90.0, 75.0)
            AnalyticsMetricType.HEART_RATE -> listOf(15.0, 30.0, 20.0, 50.0, 40.0, 70.0, 55.0)
            AnalyticsMetricType.ELEVATION -> listOf(35.0, 50.0, 42.0, 60.0, 55.0, 80.0, 70.0)
            AnalyticsMetricType.PERCENTAGE -> listOf(18.0, 40.0, 28.0, 62.0, 48.0, 82.0, 68.0)
        }
        return days.zip(values) { label, value -> AnalyticsDataPoint(label, value) }
    }

    private fun monthPoints(metric: AnalyticsMetricType): List<AnalyticsDataPoint> {
        val weeks = listOf("W1", "W2", "W3", "W4")
        val values = when (metric) {
            AnalyticsMetricType.PACE -> listOf(40.0, 55.0, 48.0, 78.0)
            AnalyticsMetricType.HEART_RATE -> listOf(30.0, 42.0, 35.0, 60.0)
            AnalyticsMetricType.ELEVATION -> listOf(45.0, 58.0, 50.0, 72.0)
            AnalyticsMetricType.PERCENTAGE -> listOf(38.0, 52.0, 44.0, 70.0)
        }
        return weeks.zip(values) { label, value -> AnalyticsDataPoint(label, value) }
    }

    private fun yearPoints(metric: AnalyticsMetricType): List<AnalyticsDataPoint> {
        val months = listOf(
            "Jan",
            "Feb",
            "Mar",
            "Apr",
            "May",
            "Jun",
            "Jul",
            "Aug",
            "Sep",
            "Oct",
            "Nov",
            "Dec"
        )
        val values = when (metric) {
            AnalyticsMetricType.PACE -> listOf(
                30.0,
                45.0,
                38.0,
                60.0,
                52.0,
                75.0,
                65.0,
                80.0,
                70.0,
                85.0,
                78.0,
                90.0
            )

            AnalyticsMetricType.HEART_RATE -> listOf(
                25.0,
                35.0,
                28.0,
                45.0,
                40.0,
                58.0,
                50.0,
                65.0,
                55.0,
                68.0,
                60.0,
                72.0
            )

            AnalyticsMetricType.ELEVATION -> listOf(
                40.0,
                52.0,
                45.0,
                62.0,
                55.0,
                72.0,
                65.0,
                78.0,
                68.0,
                82.0,
                75.0,
                88.0
            )

            AnalyticsMetricType.PERCENTAGE -> listOf(
                28.0,
                42.0,
                35.0,
                55.0,
                48.0,
                68.0,
                58.0,
                72.0,
                62.0,
                78.0,
                70.0,
                85.0
            )
        }
        return months.zip(values) { label, value -> AnalyticsDataPoint(label, value) }
    }

    fun getSummaryList(
        metricType: AnalyticsMetricType,
        period: AnalyticsPeriod
    ): List<AnalyticsSummaryData> {
        return when (metricType) {
            AnalyticsMetricType.ELEVATION -> listOf(
                AnalyticsSummaryData(
                    title = "Overall ELEVATION Climbed",
                    value = "1,090",
                    unit = "ft",
                    type = metricType,
                    dataPoints = getDataPoints(period, metricType)
                ),
                AnalyticsSummaryData(
                    title = "Total Distance Covered",
                    value = "102",
                    unit = "mi.",
                    type = metricType,
                    dataPoints = getDataPoints(period, AnalyticsMetricType.PACE)
                )
            )

            AnalyticsMetricType.PACE -> listOf(
                AnalyticsSummaryData(
                    title = "Avg PACE",
                    value = "8:45",
                    unit = "min/mi",
                    type = metricType,
                    dataPoints = getDataPoints(period, metricType)
                ),
                AnalyticsSummaryData(
                    title = "Best PACE",
                    value = "6:12",
                    unit = "min/mi",
                    type = metricType,
                    dataPoints = getDataPoints(period, metricType)
                )
            )

            AnalyticsMetricType.HEART_RATE -> listOf(
                AnalyticsSummaryData(
                    title = "Avg Heart Rate",
                    value = "142",
                    unit = "bpm",
                    type = metricType,
                    dataPoints = getDataPoints(period, metricType)
                ),
                AnalyticsSummaryData(
                    title = "Max Heart Rate",
                    value = "178",
                    unit = "bpm",
                    type = metricType,
                    dataPoints = getDataPoints(period, metricType)
                )
            )

            AnalyticsMetricType.PERCENTAGE -> listOf(
                AnalyticsSummaryData(
                    title = "Avg Effort",
                    value = "74",
                    unit = "%",
                    type = metricType,
                    dataPoints = getDataPoints(period, metricType)
                )
            )
        }


    }

    fun getAnalyticsList(): List<AnalyticsSummaryData> = AnalyticsMetricType.entries.map {
        getSummaryList(it, AnalyticsPeriod.WEEK).first()
    }
}