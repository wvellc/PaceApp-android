package net.paceapp.core.garmin

import android.content.Context
import com.garmin.android.connectiq.IQApp
import com.garmin.android.connectiq.IQDevice
import com.wvelabs.core_network.di.ApplicationScope
import com.wvelabs.core_network.utils.AppLogger
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
import net.paceapp.core.utils.AppConstants
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GarminDeviceManager @Inject constructor(
    private val garminHelper: GarminConnectHelper,
    @param:ApplicationScope private val appScope: CoroutineScope
) {
    // --- Public Exposed State ---
    val sdkStateFlow: StateFlow<GarminSdkState> = garminHelper.sdkStateFlow

    private val _activeDevice = MutableStateFlow<WatchModel?>(null)
    val activeDevice: StateFlow<WatchModel?> = _activeDevice

    private val _connectionErrors = MutableSharedFlow<String>(extraBufferCapacity = 1)
    val connectionErrors = _connectionErrors.asSharedFlow()

    private val _incomingMessages = MutableSharedFlow<List<Any>>(extraBufferCapacity = 64)
    val incomingMessages = _incomingMessages.asSharedFlow()

    // --- Internal State Tracking ---
    private var activeIqApp: IQApp? = IQApp(AppConstants.WATCH_APP_UUID)

    init {
        // 1. Observe Device Connection State
        garminHelper.deviceStatusFlow
            .onEach { (device, status) ->
                when (status) {
                    IQDevice.IQDeviceStatus.CONNECTED -> handleDeviceConnected(device)
                    IQDevice.IQDeviceStatus.NOT_CONNECTED,
                    IQDevice.IQDeviceStatus.NOT_PAIRED -> handleDeviceDisconnected(device)

                    IQDevice.IQDeviceStatus.UNKNOWN -> {}
                }
            }
            .launchIn(appScope)

//        // Observe App Info Responses
        garminHelper.appInfoFlow
            .onEach { iqApp ->
                if (iqApp != null) {
                    activeIqApp = iqApp
                    AppLogger.i("Companion App found! Attaching real-time stream listener...")

                    val currentDevice = garminHelper.getKnownDevices().find {
                        it.deviceIdentifier.toString() == _activeDevice.value?.id
                    }
                    currentDevice?.let { device ->
                        garminHelper.registerForAppEvents(device, iqApp)
                    }
                } else {
                    AppLogger.w("Garmin Device Connected but Companion App is not installed.")
                    _connectionErrors.tryEmit("PaceApp is not installed on your Garmin watch.")
                    _activeDevice.value = null
                }
            }
            .launchIn(appScope)

        // Observe Global App Messages
        garminHelper.appMessageFlow
            .onEach { (messageData, status) ->
                if (messageData != null) {
                    AppLogger.d("Pushing Garmin message payload to application stream.")
                    _incomingMessages.tryEmit(messageData)
                }
            }
            .launchIn(appScope)
    }

    // =====================================================================
    // CORE MESSAGE REGISTRATION SIDE-EFFECT ENGINE
    // =====================================================================

    private fun handleDeviceConnected(device: IQDevice) {
        if (_activeDevice.value?.id == device.deviceIdentifier.toString() &&
            _activeDevice.value?.status == WatchConnectionState.CONNECTED
        ) {
            return
        }

        garminHelper.registerForDeviceEvents(device)

        _activeDevice.value = device.toWatchModel().copy(
            status = WatchConnectionState.CONNECTED
        )

        AppLogger.d("Querying companion watch application meta-data layout...")
        garminHelper.getApplicationInfo(AppConstants.WATCH_APP_UUID, device)

    }

    private fun handleDeviceDisconnected(device: IQDevice) {
        AppLogger.w("Device disconnected or manual tear-down triggered. Purging listener registers.")

        // 1. Unregister App Messaging Hook
        activeIqApp?.let { app ->
            garminHelper.unregisterForAppEvents(device, app)
            activeIqApp = null
        }

        // 2. Unregister Device Event System Hook
        garminHelper.unregisterForDeviceEvents(device)

        // 3. Reset local states
        if (_activeDevice.value?.id == device.deviceIdentifier.toString()) {
            _activeDevice.value = null
        }

        _connectionErrors.tryEmit("Watch disconnected.")
    }

    // =====================================================================
    // PUBLIC DOMAIN CONTROLS
    // =====================================================================

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

        if (finalStatus !is GarminSdkState.Ready || savedWatchId == null) {
            _activeDevice.value = null
            return
        }

        val rawDevice = garminHelper.getKnownDevices().find {
            it.deviceIdentifier.toString() == savedWatchId
        }

        if (rawDevice != null) {
            val liveStatus = garminHelper.getLiveDeviceStatus(rawDevice)
            if (liveStatus == IQDevice.IQDeviceStatus.CONNECTED) {
                handleDeviceConnected(rawDevice)
            } else {
                _activeDevice.value = null
            }
        } else {
            _activeDevice.value = null
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

        when (liveStatus) {
            IQDevice.IQDeviceStatus.CONNECTED -> handleDeviceConnected(rawDevice)
            IQDevice.IQDeviceStatus.NOT_CONNECTED,
            IQDevice.IQDeviceStatus.NOT_PAIRED -> {
                _activeDevice.value = null
                _connectionErrors.tryEmit("Watch is disconnected. Please check Garmin Connect.")
            }

            IQDevice.IQDeviceStatus.UNKNOWN -> _activeDevice.value = null
        }
    }

    /**
     * Sends a payload to the currently connected Garmin watch.
     * @param payload A list of valid Garmin IPC types (Strings, Integers, Floats, Dictionaries)
     */
    fun sendMessageToWatch(payload:  Any) {
        val currentWatch = _activeDevice.value
        val app = activeIqApp

        if (currentWatch == null || app == null) {
            AppLogger.e("Cannot send message: Watch or App is not registered.")
            return
        }

        // Find the raw SDK device reference
        val rawDevice = garminHelper.getKnownDevices().find {
            it.deviceIdentifier.toString() == currentWatch.id
        }

        if (rawDevice == null) {
            AppLogger.e("Cannot send message: Raw device reference lost.")
            return
        }

        AppLogger.d("Attempting to send message payload: $payload")

        // Dispatch via the Helper
        garminHelper.sendMessage(rawDevice, app, payload)
    }

    fun disconnect() {
        _activeDevice.value?.let { currentWatch ->
            val rawDevice = garminHelper.getKnownDevices().find {
                it.deviceIdentifier.toString() == currentWatch.id
            }
            rawDevice?.let { handleDeviceDisconnected(it) }
        }
    }
}