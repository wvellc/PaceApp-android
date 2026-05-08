package com.example.paceapp.features.authentication.buildprofile

import android.content.Context
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.viewModelScope
import com.example.paceapp.core.base.BaseViewModel
import com.example.paceapp.core.domain.enums.GenderTypes
import com.example.paceapp.core.garmin.GarminDeviceManager
import com.example.paceapp.core.garmin.WatchConnectionState
import com.example.paceapp.core.garmin.WatchModel
import com.example.paceapp.core.garmin.state.GarminSdkState
import com.example.paceapp.features.authentication.buildprofile.BuildProfileContract.Effect
import com.example.paceapp.features.authentication.buildprofile.BuildProfileContract.Event
import com.example.paceapp.features.authentication.buildprofile.BuildProfileContract.State
import com.example.paceapp.features.authentication.buildprofile.domain.GaitPace
import com.example.paceapp.features.authentication.buildprofile.domain.ProfileStep
import com.example.paceapp.features.authentication.buildprofile.domain.ValidateBuildProfileUseCase
import com.example.paceapp.features.authentication.buildprofile.domain.getDefaultGaits
import com.example.paceapp.session.AppSessionManager
import com.wvelabs.core_network.utils.AppLogger
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class BuildProfileViewModel @Inject constructor(
    private val sessionManager: AppSessionManager,
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
            setDummyData()
        }
        val (defaultWalk, defaultRun) = currentState.selectedGender.getDefaultGaits()
        setState { copy(runningGait = defaultRun, walkingGait = defaultWalk) }
        observeFields()
        observeGarminState()
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
        //TODO:Remove this,on API integrations
        val targetStep = currentStep.nextStep
        navigateTo(targetStep)
        //TODO: Uncomment this,on API integrations
        /*if (currentStep == ProfileStep.PairWatchInit) {
            // User is ON PairWatchInit -> Fetch devices
            fetchDevicesAndProceed()
        } else {
            // Other steps, navigate forward
            val targetStep = currentStep.nextStep
            navigateTo(targetStep)

            // If current step PairWatchInit, initialize the SDK
            if (targetStep == ProfileStep.PairWatchInit) {
                initializeGarminSdk(context)
            }
        }*/
    }

    private fun initializeGarminSdk(context: Context) {
        setState { copy(isLoading = true) }
        viewModelScope.launch {
            val sdkState = garminManager.initializeGarminService(context)
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
                    copy(watchList = devices)
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
            val userData = sessionManager.getUserDetails()
            // TODO: Replace dummy data with API call
            if (userData == null) {
                throw Exception("User data not found")
            }
            sessionManager.setUserDetails(
                userData.copy(
                    firstName = currentState.firstNameState.text.trim().toString(),
                    lastName = currentState.lastNameState.text.trim().toString(),
                    gender = currentState.selectedGender,
                    id = UUID.randomUUID().toString()
                )
            )
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

