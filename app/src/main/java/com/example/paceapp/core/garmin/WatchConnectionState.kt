package com.example.paceapp.core.garmin

// Define a platform-agnostic connection state
enum class WatchConnectionState {
    CONNECTED, 
    NOT_CONNECTED, 
    UNKNOWN
}