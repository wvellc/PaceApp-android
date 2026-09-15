package net.paceapp.core.garmin

import android.content.Context
import com.garmin.android.connectiq.ConnectIQ
import com.garmin.android.connectiq.IQApp
import com.garmin.android.connectiq.IQDevice
import com.wvelabs.core_network.di.ApplicationScope
import com.wvelabs.core_network.utils.AppLogger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
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

    private val _sendMessageStatuses =
        MutableSharedFlow<ConnectIQ.IQMessageStatus>(extraBufferCapacity = 10)
    val sendMessageStatuses = _sendMessageStatuses.asSharedFlow()

    var onAppReady: (() -> Unit)? = null

    // --- Internal State Tracking ---
    private var activeIqApp: IQApp? = null
    private var registeredAppDeviceId: String? = null

    init {
        // 1. Observe Device Connection State
        garminHelper.deviceStatusFlow
            .onEach { (device, status) ->
                when (status) {
                    IQDevice.IQDeviceStatus.CONNECTED -> handleDeviceConnected(device)
                    // Still paired in Connect IQ but temporarily unreachable (Bluetooth
                    // off / out of range / watch asleep). Keep showing "connected" and keep
                    // the device-event listener alive so the SDK can reconnect on its own.
                    IQDevice.IQDeviceStatus.NOT_CONNECTED -> handleTransientDisconnect(device)
                    // Watch removed from Connect IQ — a real disconnect: tear down fully.
                    IQDevice.IQDeviceStatus.NOT_PAIRED -> handleDeviceDisconnected(device, fullTeardown = true)

                    IQDevice.IQDeviceStatus.UNKNOWN -> {}
                }
            }
            .launchIn(appScope)

//        // Observe App Info Responses
        garminHelper.appInfoFlow
            .onEach { iqApp ->
                if (iqApp != null) {
                    activeIqApp = iqApp
                    AppLogger.i("Companion App found. Attaching real-time stream listener...")
                    onAppReady?.invoke()
                    registerActiveAppEventsIfReady()
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

        garminHelper.sendMessageStatusFlow
            .onEach { status ->
                AppLogger.d("Pushing Garmin message send status to application stream: $status")
                _sendMessageStatuses.tryEmit(status)
            }
            .launchIn(appScope)
    }

    // =====================================================================
    // CORE MESSAGE REGISTRATION SIDE-EFFECT ENGINE
    // =====================================================================

    private fun handleDeviceConnected(device: IQDevice) {
        garminHelper.registerForDeviceEvents(device)
//
        // Fully established = same device shown CONNECTED AND the app layer is live. After a
        // transient drop we keep the device shown CONNECTED but clear activeIqApp, so this is
        // false and we re-establish the app layer (getApplicationInfo → app events) below.
        val wasAlreadyConnected = _activeDevice.value?.id == device.deviceIdentifier.toString() &&
                _activeDevice.value?.status == WatchConnectionState.CONNECTED &&
                activeIqApp != null
        //Safe guard
        if (wasAlreadyConnected) return

        _activeDevice.value = device.toWatchModel().copy(
            status = WatchConnectionState.CONNECTED
        )
        if (activeIqApp != null) {
            registerActiveAppEventsIfReady()
            return
        }
        garminHelper.getApplicationInfo(AppConstants.WATCH_APP_UUID, device)
    }

    // Watch temporarily unreachable but still paired in Connect IQ (Bluetooth off / out of
    // range / watch asleep). Keep the UI "connected" and keep the device-event listener
    // registered so the SDK can auto-reconnect. Drop only the app-message layer, whose IQApp
    // handle can go stale — it's re-established when the watch reconnects (handleDeviceConnected).
    private fun handleTransientDisconnect(device: IQDevice) {
        if (_activeDevice.value?.id != device.deviceIdentifier.toString()) return
        AppLogger.w("Watch temporarily unreachable; keeping it connected and listening for reconnect.")
        activeIqApp?.let { app ->
            garminHelper.unregisterForAppEvents(device, app)
        }
        activeIqApp = null
        registeredAppDeviceId = null
        // Intentionally NOT calling unregisterForDeviceEvents and NOT nulling _activeDevice.
    }

    // A real disconnect: the watch was removed from Connect IQ (NOT_PAIRED) or the user
    // explicitly disconnected. fullTeardown also drops the device-event listener so the SDK
    // stops tracking it. Only this path flips the UI to disconnected.
    private fun handleDeviceDisconnected(device: IQDevice, fullTeardown: Boolean = false) {
        AppLogger.w("Device removed or manual tear-down triggered. Purging listener registers.")

        // 1. Unregister App Messaging Hook
        activeIqApp?.let { app ->
            garminHelper.unregisterForAppEvents(device, app)
        }
        activeIqApp = null
        registeredAppDeviceId = null

        // 2. Unregister Device Event System Hook (only on a real removal / explicit disconnect)
        if (fullTeardown) {
            garminHelper.unregisterForDeviceEvents(device)
        }

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

    suspend fun getKnownDevicesAfterInit(context: Context): List<WatchModel>? =
        withContext(Dispatchers.IO) {
            val currentStatus = sdkStateFlow.value
            val finalStatus =
                currentStatus as? GarminSdkState.Ready ?: garminHelper.initializeSdk(context)

            when (finalStatus) {
                is GarminSdkState.Ready -> garminHelper.getKnownDevices().map { it.toWatchModel() }
                else -> null
            }
        }

    suspend fun restoreConnection(savedWatchId: String?, context: Context) {
        val currentStatus = sdkStateFlow.value
        val finalStatus =
            currentStatus as? GarminSdkState.Ready ?: garminHelper.initializeSdk(context)

        // No paired watch (never paired, or un-paired/cleared) → truly disconnected.
        if (finalStatus !is GarminSdkState.Ready || savedWatchId == null) {
            _activeDevice.value = null
            return
        }

        withContext(Dispatchers.IO) {
            val rawDevice = garminHelper.getKnownDevices().find {
                it.deviceIdentifier.toString() == savedWatchId
            }

            // Reachable now → (re)establish. If not reachable, DON'T clobber the current
            // state: a live "connected" display stays connected across a Bluetooth off/on
            // cycle (the request), and a cold start that has never reached the watch simply
            // stays disconnected. A real removal comes through the NOT_PAIRED event instead.
            if (rawDevice != null &&
                garminHelper.getLiveDeviceStatus(rawDevice) == IQDevice.IQDeviceStatus.CONNECTED
            ) {
                handleDeviceConnected(rawDevice)
            }
        }
    }

    fun connectDevice(watch: WatchModel) {
        appScope.launch(Dispatchers.IO) {
            val rawDevice = garminHelper.getKnownDevices().find {
                it.deviceIdentifier.toString() == watch.id
            }

            if (rawDevice == null) {
                _connectionErrors.tryEmit("Watch not found in Garmin Connect.")
                return@launch
            }

            val liveStatus = garminHelper.getLiveDeviceStatus(rawDevice)

            when (liveStatus) {
                IQDevice.IQDeviceStatus.CONNECTED -> handleDeviceConnected(rawDevice)
                IQDevice.IQDeviceStatus.NOT_CONNECTED,
                IQDevice.IQDeviceStatus.NOT_PAIRED -> {
                    _activeDevice.value = null
                    _connectionErrors.tryEmit("Watch is disconnected. Please check Garmin Connect.")
                }

                IQDevice.IQDeviceStatus.UNKNOWN -> {}
            }
        }
    }

    /**
     * Sends a payload to the currently connected Garmin watch.
     */
    fun sendMessageToWatch(payload: Any) {
        appScope.launch(Dispatchers.IO) {
            val currentWatch = _activeDevice.value
            val app = activeIqApp

            if (currentWatch == null || app == null) {
                AppLogger.e("Cannot send message: Watch or App is not registered.")
                return@launch
            }

            // Find the raw SDK device reference
            val rawDevice = garminHelper.getKnownDevices().find {
                it.deviceIdentifier.toString() == currentWatch.id
            }

            if (rawDevice == null) {
                AppLogger.e("Cannot send message: Raw device reference lost.")
                return@launch
            }

            AppLogger.d("Attempting to send message payload: $payload")
            registerActiveAppEventsIfReady()

            // Dispatch via the Helper (which also safely runs on IO now)
            garminHelper.sendMessage(rawDevice, app, payload)
        }
    }

    fun disconnect() {
        appScope.launch(Dispatchers.IO) {
            _activeDevice.value?.let { currentWatch ->
                val rawDevice = garminHelper.getKnownDevices().find {
                    it.deviceIdentifier.toString() == currentWatch.id
                }
                rawDevice?.let { handleDeviceDisconnected(it, fullTeardown = true) }
            }
        }
    }

    private fun registerActiveAppEventsIfReady() {
        val currentWatchId = _activeDevice.value?.id ?: return
        val app = activeIqApp ?: return
        val rawDevice = garminHelper.getKnownDevices().find {
            it.deviceIdentifier.toString() == currentWatchId
        } ?: return

        if (registeredAppDeviceId == currentWatchId) return

        garminHelper.registerForAppEvents(rawDevice, app)
        registeredAppDeviceId = currentWatchId
    }
}
