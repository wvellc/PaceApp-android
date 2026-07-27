package net.paceapp.core.extensions

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.ui.graphics.Color
import net.paceapp.R
import net.paceapp.core.enums.AnalyticsMetricType
import net.paceapp.theme.AppColors

val AnalyticsMetricType.titleRes: Int
    @StringRes get() = when (this) {
        AnalyticsMetricType.PACE -> R.string.avg_pace
        AnalyticsMetricType.HEART_RATE -> R.string.avg_heart_rate
        AnalyticsMetricType.PERCENTAGE -> R.string.avg_percentage
    }

// Icon Extension
val AnalyticsMetricType.iconRes: Int
    @DrawableRes get() = when (this) {
        AnalyticsMetricType.PACE -> R.drawable.ic_metrics_pace
        AnalyticsMetricType.HEART_RATE -> R.drawable.ic_metrics_heart_rate
        AnalyticsMetricType.PERCENTAGE -> R.drawable.ic_analytics_percentage
    }

// Color Extension
val AnalyticsMetricType.color: Color
    get() = when (this) {
        AnalyticsMetricType.PACE -> AppColors.FluorescentMint
        AnalyticsMetricType.HEART_RATE -> AppColors.Error
        AnalyticsMetricType.PERCENTAGE -> AppColors.FashionGray
    }