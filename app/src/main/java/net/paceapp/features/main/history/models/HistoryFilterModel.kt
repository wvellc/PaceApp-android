package net.paceapp.features.main.history.models

data class HistoryFilterModel(
    val distanceRange: ClosedFloatingPointRange<Float> = 0f..150f,
    val dateMillis: Long? = null,
    val location: String = ""
)