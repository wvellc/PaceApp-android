package net.paceapp.features.main.createevent

import androidx.compose.foundation.text.input.TextFieldState
import androidx.lifecycle.viewModelScope
import com.wvelabs.core_ui.alerts.MessageType
import com.wvelabs.core_ui.resources.ResourceProvider
import com.wvelabs.core_ui.utils.DateTimeHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import net.paceapp.core.base.BaseViewModel
import net.paceapp.core.domain.models.DistanceModel
import net.paceapp.core.enums.DistanceUnits
import net.paceapp.features.main.createevent.CreateEventContract.Effect
import net.paceapp.features.main.createevent.CreateEventContract.Event
import net.paceapp.features.main.createevent.CreateEventContract.State
import net.paceapp.features.main.createevent.enums.EventType
import net.paceapp.features.main.createevent.enums.CreateRunStep
import net.paceapp.features.main.createevent.extensions.tooltipsMessage
import net.paceapp.features.main.createevent.helpers.CreateRunStepManager
import net.paceapp.features.main.createevent.helpers.RunSegmentHelper
import net.paceapp.features.main.createevent.models.RunSegment
import net.paceapp.session.AppSessionManager
import javax.inject.Inject

@HiltViewModel
class CreateEventViewModel @Inject constructor(
    private val sessionManager: AppSessionManager,
    private val stepManager: CreateRunStepManager,
    private val resourceProvider: ResourceProvider,
    private val runSegmentHelper: RunSegmentHelper,
) : BaseViewModel<State, Event, Effect>() {
    override fun setInitialState() = State()

    override fun handleEvents(event: Event) {
        when (event) {
            is Event.Init -> initData()
            is Event.OnBackClick -> handleBackClick()
            is Event.OnNextButtonClick -> handleNextButtonClick()
            is Event.OnDateSelected -> handleDateSelected(event.millis)
            is Event.OnDistanceUpdated -> handleOnDistanceUpdated(event.distance)
            is Event.OnDurationUpdated -> handleOnDurationUpdated(event.duration)
            is Event.OnSegmentChoiceUpdated -> handleOnSegmentChoiceUpdated(event.hasSegments)
            is Event.OnSegmentCountChange -> handleSegmentCountUpdated(event.count)
            is Event.OnSegmentUpdated -> handleSegmentUpdated(event.segment)
            is Event.OnLookBackIntervalUpdated -> handleLookBackIntervalUpdated(event.interval)
            is Event.OnEventTypeUpdated -> handleEventTypeUpdated(event.type)
            is Event.OnStepInfoClick -> showStepInfoToast()
        }
    }


    private fun initData() {
        if (currentState.isInitialized) return
        if (isDebugMode) {
            setDummyData()
        }
        getDistanceUnits()
        setState { copy(isInitialized = true) }
    }

    private fun setDummyData() {
        setState {
            copy(
                eventNameState = TextFieldState(initialText = "New Run"),
                locationState = TextFieldState(initialText = "California")
            )
        }
    }

    private fun getDistanceUnits() {
        viewModelScope.launch {
            val userDetails = sessionManager.getUserDetails()
            val units = userDetails?.distanceUnits ?: DistanceUnits.MILES
            setState {
                copy(selectedDistance = selectedDistance.copy(unit = units))
            }
        }
    }

    private fun handleBackClick() {
        val state = currentState
        val prevStep = stepManager.getPreviousStep(state)

        when {
            prevStep == null -> setEffect { Effect.NavigateBack }
            prevStep == CreateRunStep.SegmentDetails && state.currentStep == CreateRunStep.SegmentDetails -> setState {
                copy(currentSegmentIndex = currentSegmentIndex - 1, segmentError = null)
            }

            else -> setState { copy(currentStep = prevStep) }
        }
    }

    private fun handleNextButtonClick() {
        val state = currentState

        val errorMessage = stepManager.validateStep(state.currentStep, state)
        if (errorMessage != null) {
            showToast(text = errorMessage, type = MessageType.Warning)
            return
        }

        when (state.currentStep) {
            CreateRunStep.SegmentCount -> handleNextFromSegmentCount(state)
            CreateRunStep.SegmentDetails -> handleNextFromSegmentDetails(state)
            else -> proceedToNextStep(state, state.segmentList)
        }
    }

    private fun handleNextFromSegmentCount(state: State) {
        val initialSegments = runSegmentHelper.generateEqualSegments(
            totalDistance = state.selectedDistance.value,
            totalDurationSeconds = state.goalTimeInSeconds,
            segmentCount = state.segmentCount
        )

        // Calculate next step
        val nextStep = stepManager.getNextStep(state)

        setState {
            copy(
                segmentList = initialSegments,
                currentStep = nextStep ?: currentStep,
                currentSegmentIndex = 0,
                segmentError = null,
            )
        }

        if (nextStep == null) createEventApi()
    }

    private fun handleNextFromSegmentDetails(state: State) {
        val (newSegments, segmentError) = runSegmentHelper.updateSegmentValues(
            segments = state.segmentList,
            segmentIndex = state.currentSegmentIndex,
            totalDistance = state.selectedDistance,
            totalDurationSeconds = state.goalTimeInSeconds,
        )

        if (segmentError != null) {
            setState { copy(segmentError = segmentError) }
            return
        }

        // Calculate next step
        val nextStep = stepManager.getNextStep(state)

        if (nextStep == CreateRunStep.SegmentDetails) {
            setState {
                copy(
                    currentSegmentIndex = currentSegmentIndex + 1,
                    segmentError = null,
                    segmentList = newSegments
                )
            }
        } else {
            // Pass the already calculated nextStep
            proceedToNextStep(state, newSegments, nextStep)
        }
    }

    private fun proceedToNextStep(
        state: State,
        updatedSegments: List<RunSegment>,
        preCalculatedNextStep: CreateRunStep? = null
    ) {
        val nextStep = preCalculatedNextStep ?: stepManager.getNextStep(state)

        setState {
            copy(
                currentStep = nextStep ?: currentStep,
                segmentList = updatedSegments,
                segmentError = null
            )
        }

        if (nextStep == null) {
            createEventApi()
        }
    }

    private fun createEventApi() {
        //TODO:SAVE DATA
        showToast(
            "${currentState.eventNameState.text.trim()} event created",
            type = MessageType.Success
        )

        setEffect { Effect.NavigateBack }
    }


    private fun handleDateSelected(millis: Long?) {
        setState {
            copy(
                selectedDate = DateTimeHelper.getUTCLocalDateTime(millis ?: 0L),
            )
        }
    }

    private fun handleOnDistanceUpdated(distance: DistanceModel) {
        setState { copy(selectedDistance = distance) }
    }

    private fun handleOnDurationUpdated(duration: Long) {
        setState { copy(goalTimeInSeconds = duration) }
    }


    private fun handleOnSegmentChoiceUpdated(hasSegments: Boolean) {
        setState { copy(hasSegments = hasSegments) }
    }

    private fun handleSegmentCountUpdated(count: Int) {
        setState { copy(segmentCount = count) }
    }

    private fun handleSegmentUpdated(segment: RunSegment) {
        val state = currentState

        val updatedList = state.segmentList.map { existingSegment ->
            if (existingSegment.id == segment.id) segment else existingSegment
        }
        setState {
            copy(
                segmentList = updatedList,
                segmentError = null
            )
        }
    }

    private fun handleLookBackIntervalUpdated(interval: Int) {
        setState { copy(lookBackInterval = interval) }
    }

    private fun handleEventTypeUpdated(type: EventType) {
        setState { copy(eventType = type) }
    }

    private fun showStepInfoToast() {
        val messageRes = currentState.currentStep.tooltipsMessage
        if (messageRes != null) {
            showToast(resourceProvider.getString(messageRes), type = MessageType.Info)
        }
    }
}
