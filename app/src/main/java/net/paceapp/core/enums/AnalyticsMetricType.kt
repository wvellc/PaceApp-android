package net.paceapp.core.enums

import kotlinx.serialization.Serializable

@Serializable
enum class AnalyticsMetricType {
    PACE,
    HEART_RATE,
    ELEVATION,
    PERCENTAGE
}