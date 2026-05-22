package net.paceapp.core.garmin

import android.content.Context
import net.paceapp.core.garmin.state.GarminSdkState
import com.garmin.android.connectiq.ConnectIQ
import com.garmin.android.connectiq.ConnectIQ.IQApplicationEventListener
import com.garmin.android.connectiq.ConnectIQ.IQDeviceEventListener
import com.garmin.android.connectiq.ConnectIQ.IQMessageStatus
import com.garmin.android.connectiq.ConnectIQ.IQSendMessageListener
import com.garmin.android.connectiq.IQApp
import com.garmin.android.connectiq.IQDevice
import com.wvelabs.core_network.utils.AppLogger
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume

@Singleton
class GarminConnectHelper @Inject constructor(
    @param:ApplicationContext private val context: Context,
    private val connectIQ: ConnectIQ
) {
    private val sdkState = MutableStateFlow<GarminSdkState>(GarminSdkState.Uninitialized)
    val sdkStateFlow: StateFlow<GarminSdkState> = sdkState.asStateFlow()

    private val initMutex = Mutex()

    suspend fun initializeSdk(activityContext: Context, autoUI: Boolean = true): GarminSdkState =
        initMutex.withLock {
            if (sdkState.value is GarminSdkState.Ready) return GarminSdkState.Ready

            sdkState.value = GarminSdkState.Initializing

            return suspendCancellableCoroutine { cont ->
                connectIQ.initialize(activityContext, autoUI, object : ConnectIQ.ConnectIQListener {
                    override fun onSdkReady() {
                        sdkState.value = GarminSdkState.Ready
                        if (cont.isActive) cont.resume(GarminSdkState.Ready)
                    }

                    override fun onInitializeError(status: ConnectIQ.IQSdkErrorStatus?) {
                        val errorState = GarminSdkState.Error(status?.name)
                        sdkState.value = errorState
                        if (cont.isActive) cont.resume(errorState)
                    }

                    override fun onSdkShutDown() {
                        try {
                            connectIQ.shutdown(context)
                        } catch (e: Exception) {
                            AppLogger.e("Garmin Shutdown Error", e)
                        } finally {
                            sdkState.value = GarminSdkState.Uninitialized
                        }
                    }
                })
            }
        }

    // --- Device Management ---

    fun getKnownDevices(): List<IQDevice> = connectIQ.knownDevices ?: emptyList()
    fun getConnectedDevices(): List<IQDevice> = connectIQ.connectedDevices ?: emptyList()

    // Remove the old transient getDeviceStatusFlow method and use this:

    /**
     * Registers a persistent application-scoped listener for device connection events.
     * This listener stays active across screen navigation until disconnectDevice is called.
     */
    fun registerPersistentDeviceListener(device: IQDevice, onStatusChanged: (IQDevice.IQDeviceStatus) -> Unit) {
        val listener = IQDeviceEventListener { _, status ->
            AppLogger.d("Garmin SDK Callback Received: $status")
            onStatusChanged(status)
        }

        try {
            // Register the callback directly into the Garmin SDK instance
            connectIQ.registerForDeviceEvents(device, listener)
        } catch (e: Exception) {
            AppLogger.e("Failed to register for Garmin device events", e)
            onStatusChanged(IQDevice.IQDeviceStatus.UNKNOWN)
        }
    }

    fun unregisterPersistentDeviceListener(device: IQDevice) {
        try {
            connectIQ.unregisterForDeviceEvents(device)
        } catch (e: Exception) {
            AppLogger.e("Failed to unregister Garmin device events", e)
        }
    }


    // --- App & Message Management ---

    /**
     * Registers a persistent listener for incoming messages from the watch app.
     * Stays alive until explicitly unregistered.
     */
    fun registerPersistentAppListener(
        device: IQDevice,
        app: IQApp,
        onMessageReceived: (List<Any>?, IQMessageStatus) -> Unit
    ) {
        val listener = IQApplicationEventListener { _, _, messageData, status ->
            AppLogger.d("Garmin App Message Received. Status: $status, Data: $messageData")
            onMessageReceived(messageData, status)
        }

        try {
            connectIQ.registerForAppEvents(device, app, listener)
        } catch (e: Exception) {
            AppLogger.e("Failed to register for Garmin app events", e)
        }
    }

    fun unregisterPersistentAppListener(device: IQDevice, app: IQApp) {
        try {
            connectIQ.unregisterForApplicationEvents(device, app)
        } catch (e: Exception) {
            AppLogger.e("Failed to unregister Garmin app events", e)
        }
    }
    /**
     * Sends a message to the watch application.
     */
    suspend fun sendMessage(device: IQDevice, app: IQApp, message: List<Any>): IQMessageStatus =
        suspendCancellableCoroutine { cont ->
            connectIQ.sendMessage(device, app, message, object : IQSendMessageListener {
                override fun onMessageStatus(
                    iqDevice: IQDevice,
                    iqApp: IQApp,
                    status: IQMessageStatus
                ) {
                    if (cont.isActive) {
                        cont.resume(status)
                    }
                }
            })
        }

    /**
     * Queries the watch to see if your specific Connect IQ App is installed.
     * Returns the IQApp instance if found, or null if it is entirely missing.
     */
    suspend fun getApplicationInfo(applicationId: String, device: IQDevice): IQApp? =
        suspendCancellableCoroutine { cont ->
            connectIQ.getApplicationInfo(
                applicationId,
                device,
                object : ConnectIQ.IQApplicationInfoListener {
                    override fun onApplicationInfoReceived(app: IQApp?) {
                        // App info was found.
                        AppLogger.e("APP_INSTALLED")
                        if (cont.isActive) cont.resume(app)
                    }

                    override fun onApplicationNotInstalled(appId: String?) {
                        // App completely missing from device
                        AppLogger.e("APP_NOT_INSTALLED")
                        if (cont.isActive) cont.resume(null)
                    }
                })
        }

    /**
     * Prompts the physical Garmin watch to launch the application.
     */
    suspend fun openApplication(device: IQDevice, app: IQApp): ConnectIQ.IQOpenApplicationStatus =
        suspendCancellableCoroutine { cont ->
            connectIQ.openApplication(
                device, app
            ) { iqDevice, iqApp, status -> if (cont.isActive) cont.resume(status) }
        }

    /**
     * Opens the Connect IQ Store app on the user's phone directly to your app's page.
     * Note: This only works when the SDK is initialized with IQConnectType.WIRELESS.
     */
    fun openStore(storeId: String) {
        connectIQ.openStore(storeId)
    }

}