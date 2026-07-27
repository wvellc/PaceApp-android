package net.paceapp.core.enums

import kotlinx.serialization.Serializable

@Serializable
// Three metrics, matching iOS AnalyticsMetricType (Pace, Heart Rate, Pace Percentage).
// iOS has no Elevation metric, so Android doesn't either (options parity).
enum class AnalyticsMetricType {
    PACE,
    HEART_RATE,
    PERCENTAGE
}