package com.example.paceapp.features.main.managewatch

import android.content.Context
import androidx.lifecycle.viewModelScope
import com.example.paceapp.R
import com.example.paceapp.core.base.BaseViewModel
import com.example.paceapp.core.garmin.GarminDeviceManager
import com.example.paceapp.core.garmin.enums.WatchConnectionState
import com.example.paceapp.core.garmin.models.WatchModel
import com.example.paceapp.features.main.managewatch.ManageWatchContract.Effect
import com.example.paceapp.features.main.managewatch.ManageWatchContract.Event
import com.example.paceapp.features.main.managewatch.ManageWatchContract.State
import com.example.paceapp.features.main.managewatch.models.ManageWatchStep
import com.wvelabs.core_network.utils.AppLogger
import com.wvelabs.core_ui.alerts.MessageType
import com.wvelabs.core_ui.resources.ResourceProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class ManageWatchViewModel @Inject constructor(
    private val garminDeviceManager: GarminDeviceManager,
    private val resourceProvider: ResourceProvider,
) : BaseViewModel<State, Event, Effect>() {

    override fun setInitialState() = State()

    override fun handleEvents(event: Event) {
        when (event) {
            is Event.Init -> initData()
            is Event.OnBackClick -> handleBackClick()

            is Event.OnWatchSelected -> handleWatchSelected(event.watch)
            is Event.OnNextButtonClick -> handleNextButtonClick(event.context)
        }
    }


    private fun initData() {
        if (currentState.isInitialized) return
        observeActiveWatchDevice()
        observeStep()
        setState { copy(isInitialized = true) }
    }


    private fun observeActiveWatchDevice() {
        garminDeviceManager.activeDevice.onEach { watch ->

            setState {
                // TODO-FIX:replace dummy watch model 'watch'
                copy(
                    selectedWatch = WatchModel(
                        id = UUID.randomUUID().toString(),
                        name = "Forerunner 245",
                        model = "Jack’s Watch",
                        status = WatchConnectionState.CONNECTED
                    ),
                    currentStep = ManageWatchStep.PairWatchSuccess,
                )
            }
//            if (watch == null) {
//                setState { copy(selectedWatch = null, currentStep = ManageWatchStep.PairWatchInit) }
//            } else {
//
//            }
        }.launchIn(viewModelScope)

    }

    private fun observeStep() {
        val buttonValidationFlow = state
            .map { Pair(it.currentStep, it.selectedWatch) }
            .distinctUntilChanged()
        observeState(buttonValidationFlow) { (step, watch) ->
            copy(
                isNextButtonEnabled = when {
                    step == ManageWatchStep.SelectModel -> watch != null
                    else -> true
                }
            )
        }

    }

    private fun handleBackClick() {
        val previous = currentState.currentStep.previousStep
        if (previous == null) {
            setEffect { Effect.NavigateBack }
            return
        }
        setState { copy(isMovingForward = false, currentStep = previous) }
    }

    private fun handleWatchSelected(watch: WatchModel) {
        setState {
            copy(
                selectedWatch = when {
                    currentState.selectedWatch?.id == watch.id -> null
                    else -> watch
                },
            )
        }
    }

    private fun handleNextButtonClick(context: Context) {
        when (currentState.currentStep) {
            ManageWatchStep.PairWatchInit -> handleStartPairingClick(context)
            ManageWatchStep.PairWatchSuccess -> handleDisconnectWatch()
            ManageWatchStep.SelectModel -> handleConfirmPairingClick()
        }
    }


    private fun handleStartPairingClick(context: Context) {
        // Safety check: Don't start if already connected
        if (currentState.selectedWatch?.status == WatchConnectionState.CONNECTED) return

        viewModelScope.launch {
            setState { copy(isLoading = true) }
            val devices = garminDeviceManager.getKnownDevicesAfterInit(context)
            setState { copy(isLoading = false) }

            if (devices != null) {
                setState {
                    copy(
                        isMovingForward = true,
                        watchList = dummyWatchList(),//TODO-FIX:Assign watch list from garmin
                        currentStep = ManageWatchStep.SelectModel
                    )
                }
            } else {
                // Handle error (e.g. Garmin Connect app not found)
                AppLogger.e("Initialization failed. Is Garmin Connect installed?")
            }
        }
    }

    private fun dummyWatchList() = listOf(
        WatchModel(
            id = UUID.randomUUID().toString(),
            name = "Forerunner 245",
            model = "Jack’s Watch",
            status = WatchConnectionState.CONNECTED
        ),
        WatchModel(
            id = UUID.randomUUID().toString(),
            name = "Forerunner 165",
            model = null,
            status = WatchConnectionState.NOT_CONNECTED
        ),
        WatchModel(
            id = UUID.randomUUID().toString(),
            name = "Forerunner 265",
            model = "Workout Watch",
            status = WatchConnectionState.UNKNOWN
        ),
    )

    private fun handleDisconnectWatch() {
//        garminDeviceManager.disconnectDevice()
        setState {
            copy(
                selectedWatch = null,
                isMovingForward = true,
                currentStep = ManageWatchStep.PairWatchInit
            )
        }
    }


    private fun handleConfirmPairingClick() {
        if (currentState.selectedWatch == null) {
            setEffect {
                Effect.ShowToast(
                    resourceProvider.getString(R.string.select_watch_error_message),
                    type = MessageType.Warning
                )
            }
            return
        }

        if (currentState.isLoading) return

        viewModelScope.launch {
            // Show loading
            setState { copy(isLoading = true) }

            // Simulate pairing delay
            delay(500)

            // garminDeviceManager.connectToDevice(currentState.selectedWatch!!)

            // Complete the action
            setEffect { Effect.ShowToast("Watch paired successfully", type = MessageType.Success) }
            setState {
                copy(
                    isLoading = false,
                    isMovingForward = true,
                    currentStep = ManageWatchStep.PairWatchSuccess
                )
            }
        }
    }

}
