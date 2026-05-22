package net.paceapp.core.extensions

import androidx.annotation.StringRes
import net.paceapp.R
import net.paceapp.core.enums.AnalyticsPeriod

val AnalyticsPeriod.titleRes: Int
    @StringRes get() = when (this) {
        AnalyticsPeriod.DAY -> R.string.day
        AnalyticsPeriod.WEEK -> R.string.week
        AnalyticsPeriod.MONTH -> R.string.month
        AnalyticsPeriod.YEAR -> R.string.year
    }