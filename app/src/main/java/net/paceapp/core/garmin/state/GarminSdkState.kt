package net.paceapp.core.garmin.state

sealed interface GarminSdkState {
    data object Uninitialized : GarminSdkState
    data object Initializing : GarminSdkState
    data object Ready : GarminSdkState
    data class Error(val message: String?) : GarminSdkState
}