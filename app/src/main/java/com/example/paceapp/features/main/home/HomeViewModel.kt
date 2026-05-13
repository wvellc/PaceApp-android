package com.example.paceapp.features.main.home

import android.content.Context
import androidx.lifecycle.viewModelScope
import com.example.paceapp.core.base.BaseViewModel
import com.example.paceapp.core.domain.usecases.ObserveUserUiModelUseCase
import com.example.paceapp.core.garmin.GarminDeviceManager
import com.example.paceapp.core.garmin.enums.WatchConnectionState
import com.example.paceapp.core.garmin.models.WatchModel
import com.example.paceapp.features.main.home.HomeContract.Effect
import com.example.paceapp.features.main.home.HomeContract.Event
import com.example.paceapp.features.main.home.HomeContract.State
import com.example.paceapp.features.main.home.models.WatchMetric
import com.wvelabs.core_network.utils.AppLogger
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val observeUserUiModelUseCase: ObserveUserUiModelUseCase,
    private val garminDeviceManager: GarminDeviceManager,
) : BaseViewModel<State, Event, Effect>() {

    override fun setInitialState() = State()

    override fun handleEvents(event: Event) {
        when (event) {
            is Event.Init -> initData()
            is Event.OnBackClick -> {
                setEffect { Effect.NavigateBack }
            }

            is Event.OnNotificationClick -> handleOnNotificationClick()
        }
    }


    private fun initData() {
        if (currentState.isInitialized) return

        observeUserData()
        observeActiveWatchDevice()
        setState { copy(isInitialized = true) }
    }


    private fun observeUserData() {
        observeUserUiModelUseCase()
            .onEach {
                setState { copy(userUiModel = it) }
            }.launchIn(viewModelScope)
    }


    private fun observeActiveWatchDevice() {
        garminDeviceManager.activeDevice.onEach { watch ->
            setState {
                copy(
                    /**TODO-FIX:replace it with
                     * watchModel = watch
                     * */
                    watchModel = WatchModel(
                        id = UUID.randomUUID().toString(),
                        name = "Forerunner 245",
                        model = "Jack’s Watch",
                        status = WatchConnectionState.CONNECTED
                    ),
                    metrics = getWatchMetrics()
                )
            }
        }.launchIn(viewModelScope)

    }

    private fun getWatchMetrics(
        bpm: String = "60",
        hrs: String = "12",
        goal: String = "-01:10",
        left: String = "07:20",
        pace: String = "9:09"
    ): List<WatchMetric> = listOf(
        WatchMetric.HeartRate(bpm),
        WatchMetric.OverallTime(hrs),
        WatchMetric.GoalTime(goal),
        WatchMetric.RemainingTime(left),
        WatchMetric.Pace(pace)
    )

    private fun handleOnNotificationClick() {
    }


    private fun handleOnStartPairing(context: Context) {
        // Safety check: Don't start if already connected
        if (currentState.watchModel?.status == WatchConnectionState.CONNECTED) return

        viewModelScope.launch {
            setState { copy(isLoading = true) }

            val devices = garminDeviceManager.getKnownDevicesAfterInit(context)

            setState { copy(isLoading = false) }

            if (devices != null) {
//                setEffect { Effect.NavigateToSelectDevice(devices) }
                //TODO:handle navigation
            } else {
                // Handle error (e.g. Garmin Connect app not found)
                AppLogger.e("Initialization failed. Is Garmin Connect installed?")

            }
        }
    }
}
