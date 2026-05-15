package com.example.paceapp.core.models

import com.example.paceapp.core.enums.AnalyticsMetricType
import java.util.UUID

data class AnalyticsSummaryData(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val value: String,
    val unit: String,
    val type: AnalyticsMetricType,
    val dataPoints: List<AnalyticsDataPoint>
)