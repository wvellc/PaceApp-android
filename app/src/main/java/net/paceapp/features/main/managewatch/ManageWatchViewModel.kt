package net.paceapp.features.main.managewatch

import android.content.Context
import androidx.lifecycle.viewModelScope
import com.wvelabs.core_ui.alerts.MessageType
import com.wvelabs.core_ui.resources.ResourceProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import net.paceapp.R
import net.paceapp.core.base.BaseViewModel
import net.paceapp.core.domain.repositories.UserRepository
import net.paceapp.core.garmin.GarminDeviceManager
import net.paceapp.core.garmin.enums.WatchConnectionState
import net.paceapp.core.garmin.models.WatchModel
import net.paceapp.features.main.managewatch.ManageWatchContract.Effect
import net.paceapp.features.main.managewatch.ManageWatchContract.Event
import net.paceapp.features.main.managewatch.ManageWatchContract.State
import net.paceapp.features.main.managewatch.models.ManageWatchStep
import javax.inject.Inject

@HiltViewModel
class ManageWatchViewModel @Inject constructor(
    private val userRepository: UserRepository,
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
                copy(
                    selectedWatch = watch, currentStep = when (watch) {
                        null -> ManageWatchStep.PairWatchInit
                        else -> ManageWatchStep.PairWatchSuccess
                    }
                )
            }
        }.launchIn(viewModelScope)

    }

    private fun observeStep() {
        val buttonValidationFlow = state.map {
            Pair(it.currentStep, it.selectedWatch)
        }.distinctUntilChanged()

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
        if (currentState.selectedWatch?.status == WatchConnectionState.CONNECTED) return

        runTask(
            onLoading = { setState { copy(isLoading = it) } },
            block = {
                val devices = garminDeviceManager.getKnownDevicesAfterInit(context)

                if (devices.isNullOrEmpty()) {
                    showToast("No paired watches found in Garmin Connect.", MessageType.Info)
                    return@runTask
                }

                if (devices.size == 1) {
                    val watch = devices.first()
                    pairWithWatch(watch)
                    setState { copy(selectedWatch = watch) }
                } else {
                    setState {
                        copy(
                            isMovingForward = true,
                            watchList = devices,
                            currentStep = ManageWatchStep.SelectModel
                        )
                    }
                }
            }
        )
    }

    private suspend fun pairWithWatch(watch: WatchModel) {
        val connectedWatch = garminDeviceManager.connectAndWait(watch)
        if (connectedWatch.status == WatchConnectionState.CONNECTED) {
            userRepository.savePairedWatchId(connectedWatch.id)
            showToast("Watch paired successfully", MessageType.Success)
        } else {
            userRepository.clearPairedWatchId()
            showToast("Connection failed. Please try again.", MessageType.Error)
        }
    }

    private fun handleDisconnectWatch() {
        runTask(
            onLoading = { loading -> setState { copy(isLoading = loading) } },
            block = {
                garminDeviceManager.disconnectDevice()
                userRepository.clearPairedWatchId()
            },
            onSuccess = {
                // Update the UI state
                setState {
                    copy(
                        selectedWatch = null,
                        isMovingForward = true,
                        currentStep = ManageWatchStep.PairWatchInit
                    )
                }
            }
        )
    }


    private fun handleConfirmPairingClick() {
        val selectedWatch = currentState.selectedWatch
        if (selectedWatch == null) {
            // Using the helper from BaseViewModel
            showToast(
                text = resourceProvider.getString(R.string.select_watch_error_message),
                type = MessageType.Warning
            )
            return
        }

        runTask(
            onLoading = { loading -> setState { copy(isLoading = loading) } },
            showSuccessMessage = "Watch paired successfully",
            block = {
                val connectedWatch = garminDeviceManager.connectAndWait(selectedWatch)
                if (connectedWatch.status != WatchConnectionState.CONNECTED) {
                    showToast("Connection failed. Please try again.", type = MessageType.Error)
                }
                userRepository.savePairedWatchId(connectedWatch.id)
            })
    }
}
