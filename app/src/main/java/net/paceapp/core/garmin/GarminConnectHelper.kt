package net.paceapp.core.garmin

import android.content.Context
import android.os.Handler
import android.os.Looper
import com.garmin.android.connectiq.ConnectIQ
import com.garmin.android.connectiq.IQApp
import com.garmin.android.connectiq.IQDevice
import com.wvelabs.core_network.utils.AppLogger
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject
import net.paceapp.core.garmin.state.GarminSdkState
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume

/**
 * Handles initialization and direct communication with the Garmin Connect IQ SDK.
 * Refactored to use purely reactive SharedFlows for asynchronous Garmin callbacks.
 */
@Singleton
class GarminConnectHelper @Inject constructor(
    @param:ApplicationContext private val context: Context,
    private val connectIQ: ConnectIQ
) {
    // --- SDK State ---
    private val _sdkState = MutableStateFlow<GarminSdkState>(GarminSdkState.Uninitialized)
    val sdkStateFlow: StateFlow<GarminSdkState> = _sdkState.asStateFlow()
    private val initMutex = Mutex()

    // --- Reactive Event Streams ---
    private val _deviceStatusFlow =
        MutableSharedFlow<Pair<IQDevice, IQDevice.IQDeviceStatus>>(extraBufferCapacity = 5)
    val deviceStatusFlow = _deviceStatusFlow.asSharedFlow()

    private val _appInfoFlow = MutableSharedFlow<IQApp?>(extraBufferCapacity = 5)
    val appInfoFlow = _appInfoFlow.asSharedFlow()

    private val _appMessageFlow =
        MutableSharedFlow<Pair<List<Any>?, ConnectIQ.IQMessageStatus>>(extraBufferCapacity = 10)
    val appMessageFlow = _appMessageFlow.asSharedFlow()

    // --- Global Memory-Safe Listeners ---
    private val deviceListener = ConnectIQ.IQDeviceEventListener { iqDevice, status ->
        AppLogger.d("Garmin SDK Fired: ${iqDevice.friendlyName} -> $status")
        _deviceStatusFlow.tryEmit(iqDevice to status)
    }

    private val appInfoListener = object : ConnectIQ.IQApplicationInfoListener {
        override fun onApplicationInfoReceived(app: IQApp?) {
            AppLogger.i("Garmin APP_INSTALLED: ${app?.applicationId}")
            _appInfoFlow.tryEmit(app)
        }

        override fun onApplicationNotInstalled(appId: String?) {
            AppLogger.w("Garmin APP_NOT_INSTALLED: $appId")
            _appInfoFlow.tryEmit(null)
        }
    }
    private val appEventListener =
        ConnectIQ.IQApplicationEventListener { device, _, messageData, status ->
            AppLogger.d("Garmin App Message from ${device.friendlyName}. Status: $status, Data: $messageData")
            _appMessageFlow.tryEmit(messageData to status)
        }

    private val sendMessageListener = ConnectIQ.IQSendMessageListener { _, _, status ->
        AppLogger.e("Message Sent: $status")
    }

    // ==========================================
    // INITIALIZATION
    // ==========================================
    suspend fun initializeSdk(activityContext: Context, autoUI: Boolean = true): GarminSdkState =
        initMutex.withLock {
            if (_sdkState.value is GarminSdkState.Ready) return GarminSdkState.Ready

            _sdkState.value = GarminSdkState.Initializing

            return suspendCancellableCoroutine { cont ->
                connectIQ.initialize(activityContext, autoUI, object : ConnectIQ.ConnectIQListener {
                    override fun onSdkReady() {
                        AppLogger.i("Garmin SDK Initialized Successfully")
                        _sdkState.value = GarminSdkState.Ready
                        if (cont.isActive) cont.resume(GarminSdkState.Ready)
                    }

                    override fun onInitializeError(status: ConnectIQ.IQSdkErrorStatus?) {
                        AppLogger.e("Garmin SDK Initialization Failed: ${status?.name}")
                        val errorState = GarminSdkState.Error(status?.name ?: "Unknown Error")
                        _sdkState.value = errorState
                        if (cont.isActive) cont.resume(errorState)
                    }

                    override fun onSdkShutDown() {
                        AppLogger.w("Garmin SDK Shutting Down")
                        try {
                            connectIQ.shutdown(context)
                        } catch (e: Exception) {
                            AppLogger.e("Garmin Shutdown Error", e)
                        } finally {
                            _sdkState.value = GarminSdkState.Uninitialized
                        }
                    }
                })
            }
        }

    // ==========================================
    // DEVICE MANAGEMENT
    // ==========================================
    fun getKnownDevices(): List<IQDevice> = try {
        connectIQ.knownDevices ?: emptyList()
    } catch (e: Exception) {
        emptyList()
    }

    fun getConnectedDevices(): List<IQDevice> = try {
        connectIQ.connectedDevices ?: emptyList()
    } catch (e: Exception) {
        emptyList()
    }

    fun registerForDeviceEvents(device: IQDevice) {
        try {
            connectIQ.registerForDeviceEvents(device, deviceListener)
        } catch (e: Exception) {
            AppLogger.e("Failed to register master listener", e)
        }
    }

    fun unregisterForDeviceEvents(device: IQDevice) {
        try {
            connectIQ.unregisterForDeviceEvents(device)
        } catch (e: Exception) {
            AppLogger.e("Failed to unregister master listener", e)
        }
    }

    fun getLiveDeviceStatus(device: IQDevice): IQDevice.IQDeviceStatus =
        try {
            connectIQ.getDeviceStatus(device)
        } catch (e: Exception) {
            IQDevice.IQDeviceStatus.UNKNOWN
        }

    // ==========================================
    // APPLICATION & MESSAGING
    // ==========================================
    fun getApplicationInfo(applicationId: String, device: IQDevice) {
        AppLogger.e("Querying application info for $applicationId")
//        _appInfoFlow.tryEmit(IQApp(applicationId))
        Handler(Looper.getMainLooper()).post {
            try {
                connectIQ.getApplicationInfo(applicationId, device, appInfoListener)
            } catch (e: Exception) {
                AppLogger.e("Failed to query Garmin application info", e)
                _appInfoFlow.tryEmit(null)
            }
        }
    }

    fun registerForAppEvents(device: IQDevice, app: IQApp) {
        try {
            connectIQ.registerForAppEvents(device, app, appEventListener)
        } catch (e: Exception) {
            AppLogger.e("Failed to register for Garmin app events", e)
        }
    }

    fun unregisterForAppEvents(device: IQDevice, app: IQApp) {
        try {
            connectIQ.unregisterForApplicationEvents(device, app)
        } catch (e: Exception) {
            AppLogger.e("Failed to unregister Garmin app events", e)
        }
    }

    fun openStore(storeId: String) {
        try {
            connectIQ.openStore(storeId)
        } catch (e: Exception) {
            AppLogger.e("Failed to open Connect IQ store", e)
        }
    }

    suspend fun openApplication(device: IQDevice, app: IQApp): ConnectIQ.IQOpenApplicationStatus? =
        suspendCancellableCoroutine { cont ->
            try {
                connectIQ.openApplication(device, app) { _, _, status ->
                    if (cont.isActive) cont.resume(status)
                }
            } catch (e: Exception) {
                AppLogger.e("Failed to prompt Garmin device to open application", e)
                if (cont.isActive) cont.resume(null)
            }
        }

    fun sendMessage(device: IQDevice, app: IQApp, message: Any) {
        try {
            connectIQ.sendMessage(device, app, message, sendMessageListener)
        } catch (e: Exception) {
            AppLogger.e("Failed to send message to Garmin app", e)
        }
    }
}


