package com.example.paceapp.core.garmin

import com.garmin.android.connectiq.IQDevice
import com.wvelabs.core_network.di.ApplicationScope
import com.wvelabs.core_network.utils.AppLogger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GarminDeviceManager @Inject constructor(
    private val garminHelper: GarminConnectHelper,
    @param:ApplicationScope private val appScope: CoroutineScope // Provide this in your DI module!
) {
    val sdkState = garminHelper.isSdkReady

    // Global State of the currently active watch
    private val _activeDevice = MutableStateFlow<IQDevice?>(null)
    val activeDevice: StateFlow<IQDevice?> = _activeDevice.asStateFlow()

    private val _deviceStatus = MutableStateFlow(IQDevice.IQDeviceStatus.UNKNOWN)
    val deviceStatus: StateFlow<IQDevice.IQDeviceStatus> = _deviceStatus.asStateFlow()

    private var statusJob: Job? = null

    suspend fun initializeGarminService(): Boolean {
        return garminHelper.initializeSdk(autoUI = true)
    }

    fun getKnownDevices(): List<IQDevice> = garminHelper.getKnownDevices()

    /**
     * Call this when the user clicks a watch in the list.
     * It sets it as the active device and globally tracks its connection status.
     */
    fun connectToDevice(device: IQDevice) {
        _activeDevice.value = device

        // Cancel any previous listening job
        statusJob?.cancel()

        // Start listening globally, tied to the app's lifecycle, not the screen's!
        statusJob = appScope.launch {
            garminHelper.getDeviceStatusFlow(device)
                .catch { AppLogger.e("Device Status Error", it) }
                .collect { status ->
                    _deviceStatus.value = status
                    if (status == IQDevice.IQDeviceStatus.NOT_CONNECTED) {
                        // Handle global disconnection logic here
                        AppLogger.e("Watch Disconnected globally!")
                    }
                }
        }
    }

    fun disconnectDevice() {
        statusJob?.cancel()
        _activeDevice.value = null
        _deviceStatus.value = IQDevice.IQDeviceStatus.UNKNOWN
    }
}