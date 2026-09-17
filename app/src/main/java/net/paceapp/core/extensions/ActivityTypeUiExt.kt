package net.paceapp.core.extensions

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import net.paceapp.R

// Maps the canonical activity-type string ("Run"/"Walking"/"Cycling"/"Other", also
// tolerating the watch's "Walk"/"Cycle" and null) to its list/upcoming row icon and its
// Event Details screen title. Mirrors iOS ActivityType.icon / .title. Run is the default.

@DrawableRes
fun activityIconRes(activityType: String?): Int = when (activityType?.trim()?.lowercase()) {
    "walk", "walking" -> R.drawable.ic_walk
    "cycle", "cycling" -> R.drawable.ic_cycle
    "other" -> R.drawable.ic_other
    else -> R.drawable.ic_runner // "run"/"running"/null
}

@StringRes
fun activityDetailsTitleRes(activityType: String?): Int = when (activityType?.trim()?.lowercase()) {
    "walk", "walking" -> R.string.walk_details
    "cycle", "cycling" -> R.string.cycle_details
    "other" -> R.string.other_details
    else -> R.string.run_details // "run"/"running"/null
}
