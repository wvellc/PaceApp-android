package com.example.paceapp.core.garmin.mappers

import com.example.paceapp.core.garmin.enums.WatchConnectionState
import com.example.paceapp.core.garmin.models.WatchModel
import com.garmin.android.connectiq.IQDevice

/**
 * Maps the Android Garmin IQDevice to the unified AppWatchDevice domain model.
 */
fun IQDevice.toWatchModel(): WatchModel {
    return WatchModel(
        id = this.deviceIdentifier.toString(),
        name = this.friendlyName ?: "Unknown Device",
        model = null, // Android doesn't have this, so we pass null
        status = when (this.status) {
            IQDevice.IQDeviceStatus.CONNECTED -> WatchConnectionState.CONNECTED
            IQDevice.IQDeviceStatus.NOT_CONNECTED -> WatchConnectionState.NOT_CONNECTED
            else -> WatchConnectionState.UNKNOWN
        }
    )
}