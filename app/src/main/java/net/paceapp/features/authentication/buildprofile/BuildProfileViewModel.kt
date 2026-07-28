package net.paceapp.features.authentication.buildprofile

import android.content.Context
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.viewModelScope
import com.wvelabs.core_network.di.ApplicationScope
import com.wvelabs.core_network.utils.AppLogger
import com.wvelabs.core_ui.alerts.MessageType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import net.paceapp.core.auth.AuthManager
import net.paceapp.core.base.BaseViewModel
import net.paceapp.core.data.firestore.GaitDocument
import net.paceapp.core.data.firestore.UserProfileRepository
import net.paceapp.core.domain.enums.GenderTypes
import net.paceapp.core.domain.models.GaitPace
import net.paceapp.core.domain.repositories.UserRepository
import net.paceapp.core.extensions.firestoreName
import net.paceapp.core.extensions.getDefaultGaits
import net.paceapp.core.extensions.watchName
import net.paceapp.core.garmin.EventSyncManager
import net.paceapp.core.garmin.GaitStrideCalculator
import net.paceapp.core.garmin.GarminDeviceManager
import net.paceapp.core.garmin.models.WatchModel
import net.paceapp.core.garmin.state.GarminSdkState
import net.paceapp.core.strava.StravaManager
import net.paceapp.features.authentication.buildprofile.BuildProfileContract.Effect
import net.paceapp.features.authentication.buildprofile.BuildProfileContract.Event
import net.paceapp.features.authentication.buildprofile.BuildProfileContract.State
import net.paceapp.features.authentication.buildprofile.domain.ValidateBuildProfileUseCase
import net.paceapp.features.authentication.buildprofile.models.ProfileStep
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class BuildProfileViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val validationUseCase: ValidateBuildProfileUseCase,
    private val garminManager: GarminDeviceManager,
    private val stravaManager: StravaManager,
    private val userProfileRepository: UserProfileRepository,
    private val authManager: AuthManager,
    private val eventSyncManager: EventSyncManager,
    @param:ApplicationScope private val appScope: CoroutineScope,
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
            is Event.OnStravaConnect -> stravaManager.connect(event.context)
        }
    }

    private fun initData() {
        if (currentState.isInitialized) return
        if (isDebugMode) {
            setDummyData()
        }

        val (defaultWalk, defaultRun) = currentState.selectedGender.getDefaultGaits()
        setState { copy(runningGait = defaultRun, walkingGait = defaultWalk) }

        observeFields()
        observeGarminState()
        observeWatchStatus()
        observeConnectionErrors()
        observeStravaState()
        setState { copy(isInitialized = true) }
    }

    // Reflect Strava connection (from users/{uid}.strava) into the step's state so the
    // Connect Strava step shows "Connected as {name}" once the OAuth round-trip lands.
    private fun observeStravaState() {
        stravaManager.startObserving()
        stravaManager.state.onEach { s ->
            setState { copy(isStravaConnected = s.isConnected, stravaAthleteName = s.athleteName) }
        }.launchIn(viewModelScope)
    }

    private fun setDummyData() {
        setState {
            copy(
                firstNameState = TextFieldState("Max"),
                lastNameState = TextFieldState("Well"),
            )
        }
    }

    private fun observeFields() {
        val textValidationFlow = snapshotFlow {
            listOf(
                currentState.firstNameState.text.toString(),
                currentState.lastNameState.text.toString(),
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
        garminManager.activeDevice.onEach { watch ->
            if (watch != null) {
                userRepository.savePairedWatchId(watch.id)
                updateState {
                    copy(
                        selectedWatch = watch,
                        isLoading = false
                    )
                }
                if (currentState.currentStep == ProfileStep.SelectModel) {
                    AppLogger.i("Successfully connected to: ${watch.name}")
                    navigateTo(currentState.currentStep.nextStep)
                }
            } else {
                updateState { copy(selectedWatch = null) }
            }
        }.launchIn(viewModelScope)
    }

    private fun observeConnectionErrors() {
        garminManager.connectionErrors.onEach { errorMessage ->
            AppLogger.e("Failed to connect to watch.")
            userRepository.clearPairedWatchId()

            updateState {
                copy(
                    selectedWatch = null,
                    isLoading = false
                )
            }

            setEffect { Effect.ShowToast(errorMessage, MessageType.Error) }

        }.launchIn(viewModelScope)
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
        updateState { copy(walkingGait = applyGaitChange(walkingGait, pace)) }
    }

    private fun setRunningGaitPace(pace: GaitPace) {
        updateState { copy(runningGait = applyGaitChange(runningGait, pace)) }
    }

    // On a unit toggle, convert the shown step-length instead of keeping the raw
    // number (matches iOS SetGaitStepView + the Settings-side UpdateGaitViewModel).
    private fun applyGaitChange(current: GaitPace, incoming: GaitPace): GaitPace {
        if (incoming.unit == current.unit) return incoming
        val converted = GaitStrideCalculator.convert(
            current.value.toDouble(),
            current.unit.firestoreName(),
            incoming.unit.firestoreName(),
        )
        return GaitPace(converted.toFloat(), incoming.unit)
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
        if (currentState.isLoading) return

        val currentStep = currentState.currentStep
        when (currentStep) {
            ProfileStep.PairWatchInit -> {
                fetchDevicesAndProceed()
                navigateTo(currentStep.nextStep)
            }

            ProfileStep.SelectModel -> {
                val watchToConnect = currentState.selectedWatch ?: return

                setState { copy(isLoading = true) }
                garminManager.connectDevice(watchToConnect)
            }

            // Footer on the final step: trigger Strava OAuth when not yet connected
            // (mirrors iOS, where the footer connects); once connected, finish onboarding.
            // "Skip" also finishes. The connection reflects live via observeStravaState().
            ProfileStep.ConnectStrava -> {
                if (currentState.isStravaConnected) {
                    navigateTo(currentStep.nextStep) // nextStep == null → saveUserDetails()
                } else {
                    stravaManager.connect(context)
                }
            }

            else -> {
                val targetStep = currentStep.nextStep
                navigateTo(targetStep)

                if (targetStep == ProfileStep.PairWatchInit) {
                    runTask(block = { garminManager.initializeGarminService(context) })
                }
            }
        }
    }

    private fun initializeGarminSdk(context: Context) {
        runTask(
            onLoading = { loading -> setState { copy(isLoading = loading) } },
            block = {
                val sdkState = garminManager.initializeGarminService(context)
                AppLogger.i("SDK_STATE - $sdkState")
                updateState { copy(garminState = sdkState) }
            }
        )
    }

    private fun fetchDevicesAndProceed() {
        if (currentState.garminState != GarminSdkState.Ready) {
            updateState { copy(showGarminSetupDialog = true) }
            return
        }
        val devices = garminManager.getKnownDevices()
        AppLogger.i("DEVICES - $devices")
        setState {
            copy(
                watchList = devices,
                selectedWatch = devices.firstOrNull(),
            )
        }
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
                    garminManager.disconnect()
                    userRepository.clearPairedWatchId()
                }
            }
            navigateTo(currentState.currentStep.skipStep)
        }
    }

    private fun handleGarminDialogSkip() {
        updateState { copy(showGarminSetupDialog = false) }
        handleOnSkipClicked()
    }

    private fun handleGarminDialogRetry(context: Context) {
        updateState { copy(showGarminSetupDialog = false) }
        initializeGarminSdk(context)
    }

    private fun navigateTo(targetStep: ProfileStep?) {
        if (targetStep == null) {
            saveUserDetails()
            return
        }
        updateState { copy(currentStep = targetStep) }
    }

    private fun saveUserDetails() {
        runTask(
            block = {
                val existingData = userRepository.getUserDetails()
                    ?: throw Exception("User data not found")

                val updatedUserData = existingData.copy(
                    firstName = currentState.firstNameState.text.trim().toString(),
                    lastName = currentState.lastNameState.text.trim().toString(),
                    gender = currentState.selectedGender,
                    walkingGait = currentState.walkingGait,
                    runningGait = currentState.runningGait,
                    id = existingData.id ?: UUID.randomUUID().toString()
                )

                userRepository.updateUserDetails(updatedUserData)

                // Onboarding gait was previously local-only. Persist it to the Firestore
                // user doc (parity with iOS) and, once onboarding is complete, push the
                // settings to the watch. Fire-and-forget on the application scope so an
                // offline Firestore write can't block/cancel finishing onboarding.
                persistGaitToCloudAndWatch(
                    walking = updatedUserData.walkingGait ?: currentState.walkingGait,
                    running = updatedUserData.runningGait ?: currentState.runningGait,
                )
            },
            onLoading = { loading -> setState { copy(isLoading = loading) } },
            onSuccess = { navigateToProfileSuccess() },
            onError = { e -> AppLogger.e("UpdateProfileError: $e") }
        )
    }

    // Writes the chosen gait to `users/{uid}.gait` and pushes the settings payload to
    // the watch. Runs only on onboarding completion (called from saveUserDetails).
    private fun persistGaitToCloudAndWatch(walking: GaitPace, running: GaitPace) {
        val uid = authManager.currentUid ?: return
        appScope.launch {
            val gait = GaitDocument(
                walkingStepLength = walking.value.toDouble(),
                walkingUnit = walking.unit.firestoreName(),
                runningStepLength = running.value.toDouble(),
                runningUnit = running.unit.firestoreName(),
            )
            runCatching { userProfileRepository.updateGait(uid, gait) }

            // Push to the watch (unit boundary: full words → "ft"/"m").
            eventSyncManager.updateSetting("walking_gait", walking.value.toDouble())
            eventSyncManager.updateSetting("walking_gait_measure", walking.unit.watchName())
            eventSyncManager.updateSetting("running_gait", running.value.toDouble())
            eventSyncManager.updateSetting("running_gait_measure", running.unit.watchName())
            eventSyncManager.sendSettings()
        }
    }

    private fun navigateToProfileSuccess() {
        setEffect { Effect.NavigateToProfileCreated }
    }
}