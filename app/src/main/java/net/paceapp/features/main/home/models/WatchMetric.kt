package net.paceapp.features.main.home.models

import androidx.annotation.DrawableRes

// One Home header capsule, built from the latest completed event. Mirrors iOS
// HomeMetric: value/unit/title/description are dynamic strings (the pace unit switches
// on measure, and the distance⇄finish capsule flips its value/unit/title on a 2.5s
// flash cadence), so they are plain Strings rather than @StringRes — the labels are the
// same literals iOS uses.
data class WatchMetric(
    val id: String,
    @param:DrawableRes val iconRes: Int,
    val value: String,
    val unit: String,
    val title: String,
    val description: String,
)
