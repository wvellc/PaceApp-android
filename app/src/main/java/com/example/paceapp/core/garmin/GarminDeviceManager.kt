package com.example.paceapp.core.garmin

import android.content.Context
import com.example.paceapp.core.garmin.enums.WatchConnectionState
import com.example.paceapp.core.garmin.mappers.toWatchModel
import com.example.paceapp.core.garmin.models.WatchModel
import com.example.paceapp.core.garmin.state.GarminSdkState
import com.garmin.android.connectiq.IQDevice
import com.wvelabs.core_network.di.ApplicationScope
import com.wvelabs.core_network.utils.AppLogger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GarminDeviceManager @Inject constructor(
    private val garminHelper: GarminConnectHelper,
    @param:ApplicationScope private val appScope: CoroutineScope
) {
    val sdkStateFlow = garminHelper.sdkStateFlow

    //Fully clean StateFlows (No IQDevice!)
    private val _activeDevice = MutableStateFlow<WatchModel?>(null)
    val activeDevice: StateFlow<WatchModel?> = _activeDevice.asStateFlow()

    private val _deviceStatus = MutableStateFlow(WatchConnectionState.UNKNOWN)
    val deviceStatus: StateFlow<WatchConnectionState> = _deviceStatus.asStateFlow()

    private var statusJob: Job? = null

    suspend fun initializeGarminService(activityContext: Context): GarminSdkState {
        return garminHelper.initializeSdk(activityContext, autoUI = true)
    }

    fun getKnownDevices(): List<WatchModel> =
        garminHelper.getKnownDevices().map { it.toWatchModel() }

    /**
     * The UI now passes the clean WatchModel.
     * We look up the raw IQDevice internally to connect!
     */
    fun connectToDevice(watch: WatchModel) {
        _activeDevice.value = watch

        // Cancel any previous listening job
        statusJob?.cancel()

        // 2. Find the actual IQDevice from the helper using the ID
        val rawDevice = garminHelper.getKnownDevices().find {
            it.deviceIdentifier.toString() == watch.id
        }

        if (rawDevice == null) {
            AppLogger.e("Could not find raw IQDevice for ID: ${watch.id}")
            return
        }

        // Start listening globally
        statusJob = appScope.launch {
            garminHelper.getDeviceStatusFlow(rawDevice)
                .catch { AppLogger.e("Device Status Error", it) }
                .collect { status ->
                    // 3. Map the SDK status to your Domain status
                    val domainStatus = when (status) {
                        IQDevice.IQDeviceStatus.CONNECTED -> WatchConnectionState.CONNECTED
                        IQDevice.IQDeviceStatus.NOT_CONNECTED -> WatchConnectionState.NOT_CONNECTED
                        else -> WatchConnectionState.UNKNOWN
                    }

                    _deviceStatus.value = domainStatus

                    // Keep the active device model's internal status perfectly in sync
                    _activeDevice.value = _activeDevice.value?.copy(status = domainStatus)

                    if (domainStatus == WatchConnectionState.NOT_CONNECTED) {
                        // Handle global disconnection logic here
                        AppLogger.e("Watch Disconnected globally!")
                    }
                }
        }
    }

    suspend fun getKnownDevicesAfterInit(context: Context): List<WatchModel>? {
        // 1. Check current status
        val currentStatus = sdkStateFlow.value

        // 2. Decide if we need to initialize
        val finalStatus = currentStatus as? GarminSdkState.Ready ?: garminHelper.initializeSdk(context)

        // 3. Return devices or null based on final outcome
        return if (finalStatus is GarminSdkState.Ready) {
            getKnownDevices()
        } else {
            null
        }
    }

    fun disconnectDevice() {
        statusJob?.cancel()
        _activeDevice.value = null
        _deviceStatus.value = WatchConnectionState.UNKNOWN
    }
}