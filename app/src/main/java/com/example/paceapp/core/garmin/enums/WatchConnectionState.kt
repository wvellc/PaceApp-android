package com.example.paceapp.core.garmin.enums

import kotlinx.serialization.Serializable

// Define a platform-agnostic connection state
@Serializable
enum class WatchConnectionState {
    CONNECTED,
    NOT_CONNECTED,
    UNKNOWN
}