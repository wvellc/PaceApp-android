package net.paceapp.features.authentication.buildprofile

import android.content.Context
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.viewModelScope
import net.paceapp.core.base.BaseViewModel
import net.paceapp.core.domain.enums.GenderTypes
import net.paceapp.core.domain.models.GaitPace
import net.paceapp.core.domain.repositories.UserRepository
import net.paceapp.core.extensions.getDefaultGaits
import net.paceapp.core.garmin.GarminDeviceManager
import net.paceapp.core.garmin.enums.WatchConnectionState
import net.paceapp.core.garmin.models.WatchModel
import net.paceapp.core.garmin.state.GarminSdkState
import net.paceapp.features.authentication.buildprofile.BuildProfileContract.Effect
import net.paceapp.features.authentication.buildprofile.BuildProfileContract.Event
import net.paceapp.features.authentication.buildprofile.BuildProfileContract.State
import net.paceapp.features.authentication.buildprofile.domain.ValidateBuildProfileUseCase
import net.paceapp.features.authentication.buildprofile.models.ProfileStep
import com.wvelabs.core_network.utils.AppLogger
import com.wvelabs.core_ui.alerts.MessageType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class BuildProfileViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val validationUseCase: ValidateBuildProfileUseCase,
    private val garminManager: GarminDeviceManager,
) : BaseViewModel<State, Event, Effect>() {
    override fun setInitialState() = State()

    override fun handleEvents(event: Event) {
        when (event) {
            is Event.Init -> initData()
            is Event.OnGenderSelected -> handleGenderSelected(event.gender)
            is Event.OnWalkingGaitChanged -> setWalkingGaitPace(event.gaitPace)
            is Event.OnRunningGaitChanged -> setRunningGaitPace(event.gaitPace)
            is Event.SelectWatchModel -> handleSelectWatchModel(event.device)
            is Event.OnNextClick -> handleNextButtonClicked(event.context)
            is Event.OnBackClick -> handleOnBackClicked()
            is Event.OnSkipClick -> handleOnSkipClicked()
            is Event.OnGarminDialogRetry -> handleGarminDialogRetry(event.context)
            is Event.OnGarminDialogSkip -> handleGarminDialogSkip()

        }
    }


    private fun initData() {
        if (currentState.isInitialized) return
        if (isDebugMode) {
            setDummyData() //TODO:Replace with garmin watch list
        }

        val (defaultWalk, defaultRun) = currentState.selectedGender.getDefaultGaits()
        setState { copy(runningGait = defaultRun, walkingGait = defaultWalk) }
        observeFields()
        observeGarminState()
        observeWatchStatus()
        setState { copy(isInitialized = true) }
    }


    private fun setDummyData() {
        setState {
            copy(
                firstNameState = TextFieldState("Max"),
                lastNameState = TextFieldState("Well"),
                watchList = listOf(
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
            )
        }
    }

    private fun observeFields() {
        val textValidationFlow = snapshotFlow {
            listOf(
                currentState.firstNameState.text.toString(),
                currentState.lastNameState.text.toString(),
                currentState.stravaLinkState.text.toString()
            )
        }

        observeState(textValidationFlow) {
            val isValid = validationUseCase(this)
            copy(isNextButtonEnabled = isValid)
        }
    }

    private fun observeGarminState() {
        viewModelScope.launch {
            garminManager.sdkStateFlow.collectLatest { sdkState ->
                updateState { copy(garminState = sdkState) }
            }
        }
    }

    private fun observeWatchStatus() {
        viewModelScope.launch {
            garminManager.activeDevice.collectLatest { watch ->
                AppLogger.e("WATCH - $watch")
                updateState { copy(selectedWatch = watch) }
            }
        }
    }

    private fun updateState(reducer: State.() -> State) {
        setState {
            val nextState = this.reducer()
            nextState.copy(isNextButtonEnabled = validationUseCase(nextState))
        }
    }

    private fun handleGenderSelected(gender: GenderTypes) {
        val (defaultWalk, defaultRun) = gender.getDefaultGaits()
        setState {
            copy(
                selectedGender = gender,
                walkingGait = defaultWalk,
                runningGait = defaultRun
            )
        }
    }

    private fun setWalkingGaitPace(pace: GaitPace) {
        updateState { copy(walkingGait = pace) }
    }

    private fun setRunningGaitPace(pace: GaitPace) {
        updateState { copy(runningGait = pace) }
    }

    private fun handleSelectWatchModel(device: WatchModel) {
        updateState {
            copy(
                selectedWatch = when {
                    currentState.selectedWatch?.id == device.id -> null
                    else -> device
                },
            )
        }
    }

    private fun handleNextButtonClicked(context: Context) {
        val currentStep = currentState.currentStep
        when (currentStep) {
            ProfileStep.PairWatchInit -> {
                fetchDevicesAndProceed()
            }

            ProfileStep.SelectModel -> {
                val watchToConnect = currentState.selectedWatch ?: return

                safeLaunch(
                    onLoading = { setState { copy(isLoading = it) } },
                    block = {
                        // This will suspend and wait for CONNECTED or NOT_CONNECTED
                        garminManager.connectAndWait(watchToConnect)
                    },
                    onSuccess = { connectedWatch ->
                        if (connectedWatch.status == WatchConnectionState.CONNECTED) {
                            AppLogger.i("Successfully connected to: ${connectedWatch.name}")
                            viewModelScope.launch {
                                // Persist explicit choice
                                userRepository.savePairedWatchId(connectedWatch.id)
                                navigateTo(currentStep.nextStep)
                            }
                        } else {
                            AppLogger.e("Failed to connect to watch.")
                            // Optional: Show a toast/error effect here
                            setEffect {
                                Effect.ShowToast(
                                    "Could not connect to watch",
                                    MessageType.Error
                                )
                            }
                        }
                    }
                )
            }

            else -> {
                val targetStep = currentStep.nextStep
                navigateTo(targetStep)

                if (targetStep == ProfileStep.PairWatchInit) {
                    // Background initialize SDK so it's ready when they click next
                    safeLaunch(block = { garminManager.initializeGarminService(context) })
                }
            }
        }
    }

    private fun initializeGarminSdk(context: Context) {
        setState { copy(isLoading = true) }
        viewModelScope.launch {
            val sdkState = garminManager.initializeGarminService(context)
            AppLogger.i("SDK_STATE - $sdkState")
            updateState { copy(isLoading = false, garminState = sdkState) }
        }
    }

    private fun fetchDevicesAndProceed() {
        //Safety check
        if (currentState.garminState != GarminSdkState.Ready) {
            updateState { copy(showGarminSetupDialog = true) }
            return
        }


        safeLaunch(
            onLoading = { loading -> setState { copy(isLoading = loading) } },
            block = { garminManager.getKnownDevices() },
            onSuccess = { devices ->
                setState {
                    copy(watchList = devices, selectedWatch = devices.firstOrNull())
                }
                // Move to the Selection screen
                navigateTo(currentState.currentStep.nextStep)
            },
        )
    }

    private fun handleOnBackClicked() {
        val previous = currentState.currentStep.previousStep
        if (previous != null) {
            navigateTo(previous)
        } else {
            setEffect { Effect.NavigateBack }
        }
    }

    private fun handleOnSkipClicked() {
        if (currentState.currentStep.isSkippable) {
            if (currentState.currentStep == ProfileStep.PairWatchInit) {
                updateState { copy(selectedWatch = null) }
                viewModelScope.launch {
                    garminManager.disconnectDevice()
                    userRepository.clearPairedWatchId()
                }
            }
            navigateTo(currentState.currentStep.skipStep)
        }
    }

    private fun handleGarminDialogSkip() {
        // Hide dialog and execute the normal skip logic
        updateState { copy(showGarminSetupDialog = false) }
        handleOnSkipClicked()
    }

    private fun handleGarminDialogRetry(context: Context) {
        // Hide dialog and re-trigger the Garmin SDK prompt
        updateState { copy(showGarminSetupDialog = false) }
        initializeGarminSdk(context)
    }

    // Navigation
    private fun navigateTo(targetStep: ProfileStep?) {
        if (targetStep == null) {
            saveUserDetails()
            return
        }
        updateState { copy(currentStep = targetStep) }
    }

    private fun saveUserDetails() {
        safeLaunch({
            val existingData = userRepository.getUserDetails()
                ?: throw Exception("User data not found")

            val updatedUserData = existingData.copy(
                firstName = currentState.firstNameState.text.trim().toString(),
                lastName = currentState.lastNameState.text.trim().toString(),
                gender = currentState.selectedGender,
                walkingGait = currentState.walkingGait,
                runningGait = currentState.runningGait,
                // Persist existing ID if it has one, otherwise generate
                id = existingData.id ?: UUID.randomUUID().toString()
            )

            userRepository.updateUserDetails(updatedUserData)
        }, onLoading = { loading ->
            setState { copy(isLoading = loading) }
        }, onSuccess = {
            navigateToProfileSuccess()
        }, onError = { e ->
            AppLogger.e("UpdateProfileError: $e")
        })
    }

    private fun navigateToProfileSuccess() {
        setEffect { Effect.NavigateToProfileCreated }
    }
}

