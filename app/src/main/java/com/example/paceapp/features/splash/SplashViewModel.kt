package com.example.paceapp.features.splash

// App-specific base classes and managers

// Screen imports
import android.content.Context
import androidx.lifecycle.viewModelScope
import com.example.paceapp.core.base.BaseViewModel
import com.example.paceapp.core.data.enums.AuthDestination
import com.example.paceapp.core.data.usecases.AuthRouteManager
import com.example.paceapp.core.garmin.GarminDeviceRepository
import com.example.paceapp.features.splash.SplashContract.Effect
import com.example.paceapp.features.splash.SplashContract.Event
import com.example.paceapp.features.splash.SplashContract.State
import com.garmin.android.connectiq.IQApp
import com.garmin.android.connectiq.IQDevice
import com.wvelabs.core_network.utils.AppLogger
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

const val WATCH_APP_ID = "bec1b23d90564b958370b9ded9266942"

@HiltViewModel
class SplashViewModel @Inject constructor(
    @param:ApplicationContext private val context: Context,
    private val authRouteManager: AuthRouteManager,
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
        if (currentState.isInitialized) return
        checkAuthentication()
        setState { copy(isInitialized = true) }
    }

    private fun checkAuthentication() {
        viewModelScope.launch {

            val destination = authRouteManager.getNextDestination()
            AppLogger.e("DESTINATION - $destination")
            when (destination) {
                AuthDestination.DASHBOARD -> setEffect { Effect.NavigateToDashboard }
                AuthDestination.BUILD_PROFILE -> setEffect { Effect.NavigateToBuildProfile }
                AuthDestination.LOGIN -> setState { copy(showGetStarted = true) }
            }
        }
    }

    private fun handleOnGetStartedClick() {
        setEffect { Effect.NavigateToLogin }
    }

    private fun initGarminService() {
        viewModelScope.launch {
            if (!garminDeviceRepository.isGarminServiceReady.value) {
                garminDeviceRepository.initializeGarminService()
            }
            val devices = garminDeviceRepository.getKnownDevices()
            AppLogger.i("DEVICES - $devices")
            for (device in devices) {
                observeDevice(device)
            }
        }
    }


    private fun observeDevice(device: IQDevice) {
        viewModelScope.launch {
            garminDeviceRepository.observeDeviceStatus(device).collect { status ->
                if (status == IQDevice.IQDeviceStatus.CONNECTED) {
                    val confirmedApp = garminDeviceRepository.getWatchAppInfo(WATCH_APP_ID, device)

                    if (confirmedApp != null) {
                        AppLogger.d("App verified on watch. Starting listener...")
                        observeApp(device, confirmedApp)
                    } else {
                        // Even if null, try creating a "fake" app object as a fallback
                        observeApp(device, IQApp(WATCH_APP_ID))
                    }

//                    app = garminDeviceRepository.getWatchAppInfo(APP_ID, device)

//                    if (appDevice != null && app != null) {
//                        if (app!!.status == IQApp.IQAppStatus.INSTALLED) {
//                            val appStatus = garminDeviceRepository.openAppOnWatch(device, app!!)
//                            AppLogger.e("APP_STATUS -$appStatus")
//                        }
//
//                        observeApp(appDevice!!, app!!)
//
//                    }
                }
            }
        }

    }

    private suspend fun observeApp(device: IQDevice, myApp: IQApp) {
        AppLogger.i("APP_NAME - ${device.friendlyName}")
        app = myApp
        appDevice = device
        garminDeviceRepository.observeDeviceMessage(device, myApp).collect { messageData ->
            AppLogger.i("MESSAGE - $messageData")
        }
    }


    private fun sendMessageToWatch() {
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
