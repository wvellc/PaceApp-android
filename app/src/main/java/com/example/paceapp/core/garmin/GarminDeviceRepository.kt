package com.example.paceapp.core.garmin

import com.garmin.android.connectiq.ConnectIQ
import com.garmin.android.connectiq.ConnectIQ.IQMessageStatus
import com.garmin.android.connectiq.IQApp
import com.garmin.android.connectiq.IQDevice
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GarminDeviceRepository @Inject constructor(
    private val garminHelper: GarminConnectHelper
) {
    // Expose the global connection state
    val isGarminServiceReady: StateFlow<Boolean> = garminHelper.isSdkReady

    suspend fun initializeGarminService() {
        garminHelper.initializeSdk(autoUI = true)
    }

    fun getKnownDevices(): List<IQDevice> = garminHelper.getKnownDevices()

    fun getConnectedDevices(): List<IQDevice> = garminHelper.getConnectedDevices()


    fun observeDeviceStatus(device: IQDevice): Flow<IQDevice.IQDeviceStatus> {
        return garminHelper.getDeviceStatusFlow(device)
    }

    fun observeDeviceMessage(
        device: IQDevice,
        app: IQApp
    ): Flow<Pair<List<Any>?, IQMessageStatus>> {
        return garminHelper.getAppMessagesFlow(device, app)
    }

    suspend fun sendMessage(device: IQDevice, app: IQApp, messages: List<Any>): IQMessageStatus {
        return garminHelper.sendMessage(device, app, message = messages)
    }

    suspend fun getWatchAppInfo(applicationId: String, device: IQDevice): IQApp? {
        return garminHelper.getApplicationInfo(applicationId, device)
    }

    suspend fun openAppOnWatch(device: IQDevice, app: IQApp): ConnectIQ.IQOpenApplicationStatus {
        return garminHelper.openApplication(device, app)
    }

    fun openConnectIQStore(storeId: String) {
        garminHelper.openStore(storeId)
    }
}