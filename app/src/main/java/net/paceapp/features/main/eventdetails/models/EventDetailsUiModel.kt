package net.paceapp.features.main.eventdetails.models

import androidx.annotation.StringRes
import kotlinx.datetime.LocalDateTime
import net.paceapp.R


data class EventDetailsUiModel(
    val id: String,
    val title: String,
    val location: String,
    val dateTime: LocalDateTime,
    
    @param:StringRes val eventTypeRes: Int,
    
    val targetPace: String,
    val totalDistance: Float,
    val distanceUnit: String,
    
    // Route & Map Data
    val encodedPolyline: String,
    val coordinates: List<CoordinateUiModel>,
    val segments: List<SegmentUiModel>
)



