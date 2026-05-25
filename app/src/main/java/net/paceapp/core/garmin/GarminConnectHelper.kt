package net.paceapp.core.garmin

import android.content.Context
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
import net.paceapp.core.garmin.state.GarminSdkState
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume

/**
 * Handles initialization and direct communication with the Garmin Connect IQ SDK.
 *
 * Created by Nikhil Dave on May 25, 2026
 */
@Singleton
class GarminConnectHelper @Inject constructor(
    @param:ApplicationContext private val context: Context,
    private val connectIQ: ConnectIQ
) {
    private val _sdkState = MutableStateFlow<GarminSdkState>(GarminSdkState.Uninitialized)
    val sdkStateFlow: StateFlow<GarminSdkState> = _sdkState.asStateFlow()

    private val _deviceStatusFlow =
        MutableSharedFlow<Pair<IQDevice, IQDevice.IQDeviceStatus>>(extraBufferCapacity = 5)
    val deviceStatusFlow = _deviceStatusFlow.asSharedFlow()

    private val initMutex = Mutex()

    private val deviceListener = ConnectIQ.IQDeviceEventListener { iqDevice, status ->
        AppLogger.d("Garmin SDK Fired: ${iqDevice.friendlyName} -> $status")
        _deviceStatusFlow.tryEmit(iqDevice to status)
    }

    suspend fun initializeSdk(activityContext: Context, autoUI: Boolean = true): GarminSdkState =
        initMutex.withLock {
            if (_sdkState.value is GarminSdkState.Ready) {
                return GarminSdkState.Ready
            }

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

    fun getKnownDevices(): List<IQDevice> {
        return try {
            connectIQ.knownDevices ?: emptyList()
        } catch (e: Exception) {
            AppLogger.e("Failed to fetch known Garmin devices", e)
            emptyList()
        }
    }

    fun getConnectedDevices(): List<IQDevice> {
        return try {
            connectIQ.connectedDevices ?: emptyList()
        } catch (e: Exception) {
            AppLogger.e("Failed to fetch connected Garmin devices", e)
            emptyList()
        }
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

    fun getLiveDeviceStatus(device: IQDevice): IQDevice.IQDeviceStatus {
        return try {
            connectIQ.getDeviceStatus(device)
        } catch (e: Exception) {
            AppLogger.e("Failed to get live device status", e)
            IQDevice.IQDeviceStatus.UNKNOWN
        }
    }

    suspend fun getApplicationInfo(applicationId: String, device: IQDevice): IQApp? =
        suspendCancellableCoroutine { cont ->
            try {
                connectIQ.getApplicationInfo(
                    applicationId,
                    device,
                    object : ConnectIQ.IQApplicationInfoListener {
                        override fun onApplicationInfoReceived(app: IQApp?) {
                            if (cont.isActive) cont.resume(app)
                        }

                        override fun onApplicationNotInstalled(appId: String?) {
                            if (cont.isActive) cont.resume(null)
                        }
                    })
            } catch (e: Exception) {
                AppLogger.e("Failed to query Garmin application info", e)
                if (cont.isActive) cont.resume(null)
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

    fun openStore(storeId: String) {
        try {
            connectIQ.openStore(storeId)
        } catch (e: Exception) {
            AppLogger.e("Failed to open Connect IQ store", e)
        }
    }

    suspend fun sendMessage(
        device: IQDevice,
        app: IQApp,
        message: List<Any>
    ): ConnectIQ.IQMessageStatus =
        suspendCancellableCoroutine { cont ->
            try {
                connectIQ.sendMessage(device, app, message) { _, _, status ->
                    if (cont.isActive) cont.resume(status)
                }
            } catch (e: Exception) {
                AppLogger.e("Failed to send message to Garmin app", e)
                if (cont.isActive) cont.resume(ConnectIQ.IQMessageStatus.FAILURE_UNKNOWN)
            }
        }

    fun registerForAppEvents(
        device: IQDevice,
        app: IQApp,
        onMessageReceived: (List<Any>?, ConnectIQ.IQMessageStatus) -> Unit
    ) {
        try {
            connectIQ.registerForAppEvents(device, app) { _, _, messageData, status ->
                AppLogger.d("Garmin App Message Received. Status: $status, Data: $messageData")
                onMessageReceived(messageData, status)
            }
        } catch (e: Exception) {
            AppLogger.e("Failed to register for Garmin app events", e)
            onMessageReceived(null, ConnectIQ.IQMessageStatus.FAILURE_UNKNOWN)
        }
    }

    fun unregisterForAppEvents(device: IQDevice, app: IQApp) {
        try {
            connectIQ.unregisterForApplicationEvents(device, app)
        } catch (e: Exception) {
            AppLogger.e("Failed to unregister Garmin app events", e)
        }
    }
}