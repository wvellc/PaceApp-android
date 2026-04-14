package com.example.paceapp.features.splash

// App-specific base classes and managers

// Screen imports
import androidx.lifecycle.viewModelScope
import com.example.paceapp.core.base.BaseViewModel
import com.example.paceapp.core.garmin.GarminDeviceRepository
import com.example.paceapp.features.splash.SplashContract.Effect
import com.example.paceapp.features.splash.SplashContract.Event
import com.example.paceapp.features.splash.SplashContract.State
import com.example.paceapp.session.AppSessionManager
import com.garmin.android.connectiq.IQApp
import com.garmin.android.connectiq.IQDevice
import com.wvelabs.core_network.utils.AppLogger
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

const val APP_ID = "bec1b23d90564b958370b9ded9266942"

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val sessionManager: AppSessionManager,
    private val garminDeviceRepository: GarminDeviceRepository,
) : BaseViewModel<State, Event, Effect>() {
    var app: IQApp? = null
    var appDevice: IQDevice? = null
    override fun setInitialState() = State()

    override fun handleEvents(event: Event) {
        when (event) {
            is Event.Init -> initData()
            is Event.OnGetStarted -> handleOnGetStartedClick()
        }
    }

    private fun initData() {
        if (state.value.isInitialized) return
        setState { copy(isInitialized = true) }
        initGarminService()
    }

    private fun initGarminService() {
        viewModelScope.launch {
            if (!garminDeviceRepository.isGarminServiceReady.value) {
                garminDeviceRepository.initializeGarminService()
            }
            val device = garminDeviceRepository.getKnownDevices()

            AppLogger.i("DEVICES - $device")
            if (device.isNotEmpty()) {
                observeDevice(device.first())
            }
        }
    }


    private fun observeDevice(device: IQDevice) {
        viewModelScope.launch {
            appDevice = device
            garminDeviceRepository.observeDeviceStatus(device).collect { status ->
                if (status == IQDevice.IQDeviceStatus.CONNECTED) {
                    app = garminDeviceRepository.getWatchAppInfo(APP_ID, device)
                    AppLogger.e("APP_INFO - ${app?.applicationId} NAME - ${app?.displayName}")
                    if (appDevice != null && app != null) {
                        observeApp(appDevice!!, app!!)

//                        if (app.status == IQApp.IQAppStatus.INSTALLED) {
//                            val appStatus = garminDeviceRepository.openAppOnWatch(device, app)
//                            AppLogger.e("APP_STATUS -$appStatus")
//                        }
                    }
                }
            }
        }

    }

    private suspend fun observeApp(device: IQDevice, app: IQApp) {
        AppLogger.i("APP_NAME - ${app.displayName}")
        garminDeviceRepository.observeDeviceMessage(device, app).collect { messageData ->
        }
    }


    private fun handleOnGetStartedClick() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                if (appDevice != null && app != null) {
                    garminDeviceRepository.sendMessage(
                        device = appDevice!!,
                        app = app!!,
                        messages = listOf(
                            "new message 0",
                            "new message 1",
                            "new message 2",
                            "new message 3"
                        )

                    )
                }
            }

        }

    }
}
