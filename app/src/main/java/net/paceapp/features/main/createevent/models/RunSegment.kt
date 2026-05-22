package net.paceapp.features.main.createevent.models

import androidx.annotation.StringRes
import net.paceapp.R

data class RunSegment(
    val id: Int,
    val distance: Float = 0f,
    val goalHours: Int = 0,
    val goalMinutes: Int = 0,
    val goalSeconds: Int = 0
) {
    val totalGoalSeconds: Int
        get() = (goalHours * 3600) + (goalMinutes * 60) + goalSeconds
}
