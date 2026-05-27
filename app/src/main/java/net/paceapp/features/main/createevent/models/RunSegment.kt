package net.paceapp.features.main.createevent.models

data class RunSegment(
    val id: Int,
    val distance: Float = 0f,
    val durationInSeconds: Long = 0,
)