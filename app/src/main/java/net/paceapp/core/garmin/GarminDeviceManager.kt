package net.paceapp.core.garmin

import android.content.Context
import com.garmin.android.connectiq.ConnectIQ
import com.garmin.android.connectiq.IQApp
import com.garmin.android.connectiq.IQDevice
import com.wvelabs.core_network.di.ApplicationScope
import com.wvelabs.core_network.utils.AppLogger
import kotlinx.coroutines.CancellableContinuation
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
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
    val sdkStateFlow = garminHelper.sdkStateFlow

    // Single source of truth for the active watch and its connection status
    private val _activeDevice = MutableStateFlow<WatchModel?>(null)
    val activeDevice: StateFlow<WatchModel?> = _activeDevice.asStateFlow()

    private val _incomingMessages = MutableSharedFlow<List<Any>?>(extraBufferCapacity = 10)
    val incomingMessages = _incomingMessages.asSharedFlow()

    private var currentlyTrackedRawApp: IQApp? = null
    private var currentlyTrackedRawDevice: IQDevice? = null
    private val connectionMutex = Mutex()
    private var pendingConnectionContinuation: CancellableContinuation<WatchModel>? = null

    fun getKnownDevices(): List<WatchModel> =
        garminHelper.getKnownDevices().map { it.toWatchModel() }

    fun getConnectedDevices(): List<WatchModel> =
        garminHelper.getConnectedDevices().map { it.toWatchModel() }

    suspend fun connectAndWait(watch: WatchModel): WatchModel {
        safelyUnregisterCurrent()

        val rawDevice = garminHelper.getKnownDevices().find {
            it.deviceIdentifier.toString() == watch.id
        }

        if (rawDevice == null) {
            val failedState = watch.copy(status = WatchConnectionState.NOT_CONNECTED)
            _activeDevice.value = failedState
            return failedState
        }

        currentlyTrackedRawDevice = rawDevice

        val immediateDomainStatus = when (rawDevice.status) {
            IQDevice.IQDeviceStatus.CONNECTED -> WatchConnectionState.CONNECTED
            IQDevice.IQDeviceStatus.NOT_CONNECTED -> WatchConnectionState.NOT_CONNECTED
            else -> WatchConnectionState.UNKNOWN
        }


        if (immediateDomainStatus == WatchConnectionState.CONNECTED ||
            immediateDomainStatus == WatchConnectionState.NOT_CONNECTED
        ) {

            val instantWatch = watch.copy(status = immediateDomainStatus)
            _activeDevice.value = instantWatch

            setupPersistentListener(rawDevice)

            return instantWatch
        }

        _activeDevice.value = watch.copy(status = WatchConnectionState.UNKNOWN)
        setupPersistentListener(rawDevice)

        val finalWatch = activeDevice.first { state ->
            state?.status == WatchConnectionState.CONNECTED ||
                    state?.status == WatchConnectionState.NOT_CONNECTED
        }

        return finalWatch ?: watch.copy(status = WatchConnectionState.NOT_CONNECTED)
    }

    /**
     * Updates restore logic to only auto-connect to a watch the user
     * has explicitly paired with PaceApp.
     */
    fun restoreExistingConnection(savedWatchId: String?): WatchModel? {
        if (savedWatchId == null) return null // User hasn't paired a watch or they clicked "Skip"

        val connectedRawDevices = garminHelper.getConnectedDevices()

        // Only restore the device if its ID matches the one saved in PaceApp
        val explicitlyPairedDevice = connectedRawDevices.find {
            it.deviceIdentifier.toString() == savedWatchId
        } ?: return null

        val watchModel =
            explicitlyPairedDevice.toWatchModel().copy(status = WatchConnectionState.CONNECTED)

        _activeDevice.value = watchModel
        currentlyTrackedRawDevice = explicitlyPairedDevice

        setupPersistentListener(explicitlyPairedDevice)

        return watchModel
    }


    private fun setupPersistentListener(rawDevice: IQDevice) {
        // No more callback parameters needed here!

        garminHelper.registerPersistentDeviceListener(rawDevice) { status ->
            appScope.launch {
                val domainStatus = when (status) {
                    IQDevice.IQDeviceStatus.CONNECTED -> WatchConnectionState.CONNECTED
                    IQDevice.IQDeviceStatus.NOT_CONNECTED -> WatchConnectionState.NOT_CONNECTED
                    else -> WatchConnectionState.UNKNOWN
                }

                // Guard against Garmin SDK randomly emitting UNKNOWN when already connected
                if (domainStatus == WatchConnectionState.UNKNOWN && _activeDevice.value?.status == WatchConnectionState.CONNECTED) {
                    AppLogger.w("Ignored an UNKNOWN blip while device is CONNECTED.")
                    return@launch
                }

                val currentWatch = _activeDevice.value ?: rawDevice.toWatchModel()
                val updatedWatch = currentWatch.copy(status = domainStatus)

                // Pushing to this flow automatically unblocks `connectAndWait` above,
                // AND instantly updates your Home and Manage Watch screens!
                _activeDevice.value = updatedWatch

                // Handle the app bindings
                if (domainStatus == WatchConnectionState.CONNECTED) {
                    bindAppMessageListener()
                } else {
                    safelyUnregisterApp()
                }
            }
        }
    }

    private fun bindAppMessageListener() {
        val rawDevice = currentlyTrackedRawDevice ?: return

        // Manually instantiate the app to bypass slow Garmin Connect metadata sync
        val iqApp = IQApp(AppConstants.WATCH_APP_UUID)

        safelyUnregisterApp()
        currentlyTrackedRawApp = iqApp

        garminHelper.registerPersistentAppListener(rawDevice, iqApp) { data, status ->
            if (status == ConnectIQ.IQMessageStatus.SUCCESS) {
                _incomingMessages.tryEmit(data)
            } else {
                AppLogger.e("Message reception failed with status: $status")
            }
        }

        AppLogger.i("Global App Message Listener Successfully Bound!")
    }

    suspend fun sendMessageToWatch(message: List<Any>): Boolean {
        val rawDevice = currentlyTrackedRawDevice
        val app = currentlyTrackedRawApp

        if (rawDevice == null || app == null) {
            AppLogger.e("Cannot send message: Device or App not initialized.")
            return false
        }

        val status = garminHelper.sendMessage(rawDevice, app, message)
        return status == ConnectIQ.IQMessageStatus.SUCCESS
    }

    // Update the bootloader to accept the saved ID
    suspend fun onAppLaunchRestore(context: Context, savedWatchId: String?): WatchModel? {
        if (savedWatchId == null) {
            AppLogger.i("App Launched: No watch paired in PaceApp. Skipping auto-restore.")
            return null
        }

        val sdkState = initializeGarminService(context)

        if (sdkState is GarminSdkState.Ready) {
            val restoredWatch = restoreExistingConnection(savedWatchId)

            if (restoredWatch != null) {
                AppLogger.i("App Launched: Silently restored connection to ${restoredWatch.name}")
            } else {
                AppLogger.i("App Launched: Saved watch ($savedWatchId) is not currently connected via Bluetooth.")
            }

            return restoredWatch
        }

        return null
    }

    fun disconnectDevice() {
        safelyUnregisterApp()
        safelyUnregisterCurrent()
        _activeDevice.value = null
    }

    private fun safelyUnregisterApp() {
        val device = currentlyTrackedRawDevice
        val app = currentlyTrackedRawApp
        if (device != null && app != null) {
            garminHelper.unregisterPersistentAppListener(device, app)
        }
        currentlyTrackedRawApp = null
    }

    private fun safelyUnregisterCurrent() {
        currentlyTrackedRawDevice?.let { device ->
            garminHelper.unregisterPersistentDeviceListener(device)
        }
        currentlyTrackedRawDevice = null
    }

    suspend fun initializeGarminService(activityContext: Context): GarminSdkState {
        return garminHelper.initializeSdk(activityContext, autoUI = true)
    }

    suspend fun getKnownDevicesAfterInit(context: Context): List<WatchModel>? {
        val currentStatus = sdkStateFlow.value
        val finalStatus =
            currentStatus as? GarminSdkState.Ready ?: garminHelper.initializeSdk(context)
        return if (finalStatus is GarminSdkState.Ready) getKnownDevices() else null
    }
}