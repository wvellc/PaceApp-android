package com.example.paceapp.core.garmin

data class WatchModel(
    val id: String,
    val name: String,
    val model: String?, // Nullable because Android lacks this property
    val status: WatchConnectionState
)