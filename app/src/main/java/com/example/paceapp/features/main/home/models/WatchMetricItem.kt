package com.example.paceapp.features.main.home.models

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.example.paceapp.R

sealed interface WatchMetric {
    val value: String

    @get:DrawableRes
    val iconRes: Int

    @get:StringRes
    val unitLabelRes: Int

    @get:StringRes
    val titleRes: Int

    @get:StringRes
    val descriptionRes: Int

    data class HeartRate(override val value: String) : WatchMetric {
        override val iconRes = R.drawable.ic_metrics_heart_rate
        override val unitLabelRes = R.string.unit_heart_rate
        override val titleRes = R.string.title_heart_rate
        override val descriptionRes = R.string.desc_heart_rate
    }

    data class OverallTime(override val value: String) : WatchMetric {
        override val iconRes = R.drawable.ic_metrics_overall_time
        override val unitLabelRes = R.string.unit_overall_time
        override val titleRes = R.string.title_overall_time
        override val descriptionRes = R.string.desc_overall_time
    }

    data class GoalTime(override val value: String) : WatchMetric {
        override val iconRes = R.drawable.ic_metrics_goal_time
        override val unitLabelRes = R.string.unit_m_per_sec
        override val titleRes = R.string.title_goal_time
        override val descriptionRes = R.string.desc_goal_time
    }

    data class RemainingTime(override val value: String) : WatchMetric {
        override val iconRes = R.drawable.ic_metrics_remaining_time
        override val unitLabelRes = R.string.unit_m_per_sec
        override val titleRes = R.string.title_remaining_time
        override val descriptionRes = R.string.desc_remaining_time
    }

    data class Pace(override val value: String) : WatchMetric {
        override val iconRes = R.drawable.ic_metrics_pace
        override val unitLabelRes = R.string.unit_pace
        override val titleRes = R.string.title_pace
        override val descriptionRes = R.string.desc_pace
    }
}