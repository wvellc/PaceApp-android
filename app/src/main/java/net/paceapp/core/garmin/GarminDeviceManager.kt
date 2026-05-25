package net.paceapp.core.garmin

import android.content.Context
import com.garmin.android.connectiq.IQDevice
import com.wvelabs.core_network.di.ApplicationScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import net.paceapp.core.garmin.enums.WatchConnectionState
import net.paceapp.core.garmin.mappers.toWatchModel
import net.paceapp.core.garmin.models.WatchModel
import net.paceapp.core.garmin.state.GarminSdkState
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GarminDeviceManager @Inject constructor(
    private val garminHelper: GarminConnectHelper,
    @param:ApplicationScope private val appScope: CoroutineScope
) {
    val sdkStateFlow: StateFlow<GarminSdkState> = garminHelper.sdkStateFlow

    private val _activeDevice = MutableStateFlow<WatchModel?>(null)
    val activeDevice: StateFlow<WatchModel?> = _activeDevice

    private val _connectionErrors = MutableSharedFlow<String>(extraBufferCapacity = 1)
    val connectionErrors = _connectionErrors.asSharedFlow()

    init {
        garminHelper.deviceStatusFlow
            .onEach { (device, status) ->
                when (status) {
                    IQDevice.IQDeviceStatus.CONNECTED -> {
                        // Force the correct state to override any stale mapper cache
                        _activeDevice.value = device.toWatchModel().copy(
                            status = WatchConnectionState.CONNECTED
                        )
                    }

                    IQDevice.IQDeviceStatus.NOT_CONNECTED,
                    IQDevice.IQDeviceStatus.NOT_PAIRED -> {
                        if (_activeDevice.value?.id == device.deviceIdentifier.toString()) {
                            _activeDevice.value = null
                        }

                        _connectionErrors.tryEmit("Connection failed. Please check your device in Garmin Connect.")
                        garminHelper.unregisterForDeviceEvents(device)
                    }

                    IQDevice.IQDeviceStatus.UNKNOWN -> {}
                }
            }
            .launchIn(appScope)
    }

    suspend fun initializeGarminService(activityContext: Context): GarminSdkState {
        return garminHelper.initializeSdk(activityContext, autoUI = true)
    }

    fun getKnownDevices() = garminHelper.getKnownDevices().map { it.toWatchModel() }

    suspend fun getKnownDevicesAfterInit(context: Context): List<WatchModel>? {
        val currentStatus = sdkStateFlow.value
        val finalStatus =
            currentStatus as? GarminSdkState.Ready ?: garminHelper.initializeSdk(context)

        return if (finalStatus is GarminSdkState.Ready) {
            garminHelper.getKnownDevices().map { it.toWatchModel() }
        } else null
    }

    suspend fun restoreConnection(savedWatchId: String?, context: Context) {
        val currentStatus = sdkStateFlow.value
        val finalStatus =
            currentStatus as? GarminSdkState.Ready ?: garminHelper.initializeSdk(context)

        if (finalStatus !is GarminSdkState.Ready) return

        if (savedWatchId == null) {
            _activeDevice.value = null
            return
        }
        val rawDevice = garminHelper.getKnownDevices().find {
            it.deviceIdentifier.toString() == savedWatchId
        }

        if (rawDevice != null) {
            val liveStatus = garminHelper.getLiveDeviceStatus(rawDevice)
            garminHelper.registerForDeviceEvents(rawDevice)

            if (liveStatus == IQDevice.IQDeviceStatus.CONNECTED) {
                // Force the correct state to override any stale mapper cache
                _activeDevice.value = rawDevice.toWatchModel().copy(
                    status = WatchConnectionState.CONNECTED
                )
            } else {
                _activeDevice.value = null
            }
        }
    }

    fun connectDevice(watch: WatchModel) {
        val rawDevice = garminHelper.getKnownDevices().find {
            it.deviceIdentifier.toString() == watch.id
        }

        if (rawDevice == null) {
            _connectionErrors.tryEmit("Watch not found in Garmin Connect.")
            return
        }
        val liveStatus = garminHelper.getLiveDeviceStatus(rawDevice)
        garminHelper.registerForDeviceEvents(rawDevice)

        when (liveStatus) {
            IQDevice.IQDeviceStatus.CONNECTED -> {
                // Force the correct state to override any stale mapper cache
                _activeDevice.value = rawDevice.toWatchModel().copy(
                    status = WatchConnectionState.CONNECTED
                )
            }

            IQDevice.IQDeviceStatus.NOT_CONNECTED,
            IQDevice.IQDeviceStatus.NOT_PAIRED -> {
                _activeDevice.value = null
                _connectionErrors.tryEmit("Watch is disconnected. Please check Garmin Connect.")
            }

            IQDevice.IQDeviceStatus.UNKNOWN -> {
                _activeDevice.value = null
            }
        }
    }

    fun disconnect() {
        _activeDevice.value?.let { currentWatch ->
            val rawDevice = garminHelper.getKnownDevices().find {
                it.deviceIdentifier.toString() == currentWatch.id
            }
            rawDevice?.let { garminHelper.unregisterForDeviceEvents(it) }
        }

        _activeDevice.value = null
    }
}