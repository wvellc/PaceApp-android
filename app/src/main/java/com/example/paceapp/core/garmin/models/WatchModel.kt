package com.example.paceapp.core.garmin.models

import com.example.paceapp.core.garmin.enums.WatchConnectionState
import kotlinx.serialization.Serializable

@Serializable
data class WatchModel(
    val id: String,
    val name: String,
    val model: String?, // Nullable because Android lacks this property
    val status: WatchConnectionState
)