package com.example.paceapp.core.garmin

import com.garmin.android.connectiq.ConnectIQ
import com.garmin.android.connectiq.IQApp

sealed class WatchAppState {
    object Idle : WatchAppState()
    object Checking : WatchAppState()
    data class Installed(val app: IQApp) : WatchAppState()
    object UpdateRequired : WatchAppState()
    object NotInstalled : WatchAppState()
    object NotSupported : WatchAppState()
    data class OpenStatus(val status: ConnectIQ.IQOpenApplicationStatus) : WatchAppState()
}