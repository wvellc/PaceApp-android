package net.paceapp.core.models

data class ActivityUiModel(
    val id: String,
    val title: String,
    val date: String,
    val location: String,
    val distance: String,
    val goalTime: String,
    val avgPace: String,
    val paceDifference: String,
    val isAheadOfTime: Boolean
)