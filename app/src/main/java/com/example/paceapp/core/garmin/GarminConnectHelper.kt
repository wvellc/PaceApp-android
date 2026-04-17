package com.example.paceapp.core.garmin

import android.content.Context
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
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume

@Singleton
class GarminConnectHelper @Inject constructor(
    @ApplicationContext private val context: Context, // Crucial for Singleton memory safety
    private val connectIQ: ConnectIQ
) {
    // Global state reflecting the SDK's readiness across the entire app
    private val _isSdkReady = MutableStateFlow(false)
    val isSdkReady: StateFlow<Boolean> = _isSdkReady.asStateFlow()

    /**
     * Initializes the Garmin SDK. Safe to call multiple times.
     * @param autoUI If true, Garmin SDK will automatically prompt the user to install
     * the Garmin Connect app if it is missing from their phone.
     */
    suspend fun initializeSdk(autoUI: Boolean = true): Boolean {
        // Prevent re-initialization if already connected
        if (_isSdkReady.value) return true

        return suspendCancellableCoroutine { cont ->
            connectIQ.initialize(context, autoUI, object : ConnectIQ.ConnectIQListener {
                override fun onSdkReady() {
                    _isSdkReady.value = true
                    if (cont.isActive) cont.resume(true)
                }

                override fun onInitializeError(status: ConnectIQ.IQSdkErrorStatus?) {
                    _isSdkReady.value = false
                    if (cont.isActive) cont.resume(false)
                }

                override fun onSdkShutDown() {
                    // The Garmin service was killed or shut down. 
                    // Updating this state will instantly update any UI observing it.
                    try {
                        connectIQ.shutdown(context)
                        _isSdkReady.value = false // Reset the state
                    } catch (e: Exception) {
                        // Catch any lingering Garmin SDK errors during shutdown
                        e.printStackTrace()
                    }
                }
            })
        }
    }

    // --- Device Management ---

    fun getKnownDevices(): List<IQDevice> = connectIQ.knownDevices ?: emptyList()
    fun getConnectedDevices(): List<IQDevice> = connectIQ.connectedDevices ?: emptyList()

    /**
     * Observe connection status for a specific device.
     * Automatically unregisters when the Flow collector cancels (e.g., Composable leaves screen).
     */
    fun getDeviceStatusFlow(device: IQDevice): Flow<IQDevice.IQDeviceStatus> = callbackFlow {
        val listener = IQDeviceEventListener { iqDevice, status ->
            AppLogger.d("Device Status Changed: $status")
            trySend(status)
        }

        connectIQ.registerForDeviceEvents(device, listener)
        trySend(device.status) // Emit current status immediately

        awaitClose {
            connectIQ.unregisterForDeviceEvents(device)
        }
    }

    // --- App & Message Management ---

    /**
     * Observe messages coming from a specific ConnectIQ App on the watch.
     */
    fun getAppMessagesFlow(device: IQDevice, app: IQApp): Flow<Pair<List<Any>?, IQMessageStatus>> =
        callbackFlow {
            val listener = IQApplicationEventListener { iqDevice, iqApp, messageData, status ->
                AppLogger.e("SUCCESS!! RECEIVED: $messageData (Type: ${messageData?.javaClass?.simpleName})")
                trySend(
                    Pair(
                        messageData,
                        status
                    )
                )
            }

            connectIQ.registerForAppEvents(device, app, listener)

            awaitClose {
                connectIQ.unregisterForApplicationEvents(device, app)
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
                        // App info was found. (Note: You still need to check app.status)
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