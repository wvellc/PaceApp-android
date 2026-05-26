package net.paceapp.features.main.createevent

import androidx.lifecycle.viewModelScope
import com.wvelabs.core_ui.alerts.MessageType
import com.wvelabs.core_ui.utils.DateTimeHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import net.paceapp.core.base.BaseViewModel
import net.paceapp.core.domain.models.DistanceModel
import net.paceapp.core.enums.DistanceUnits
import net.paceapp.features.main.createevent.CreateEventContract.Effect
import net.paceapp.features.main.createevent.CreateEventContract.Event
import net.paceapp.features.main.createevent.CreateEventContract.State
import net.paceapp.features.main.createevent.enums.CreateRunStep
import net.paceapp.features.main.createevent.mangers.CreateRunStepManager
import net.paceapp.session.AppSessionManager
import javax.inject.Inject

@HiltViewModel
class CreateEventViewModel @Inject constructor(
    private val sessionManager: AppSessionManager,
    private val stepManager: CreateRunStepManager,
) : BaseViewModel<State, Event, Effect>() {
    private val stepList by lazy { CreateRunStep.entries.toMutableList() }

    override fun setInitialState() = State()

    override fun handleEvents(event: Event) {
        when (event) {
            is Event.Init -> initData()
            is Event.OnBackClick -> handleBackClick()
            is Event.OnNextButtonClick -> handleNextButtonClick()
            is Event.OnDateSelected -> handleDateSelected(event.millis)
            is Event.OnDistanceUpdated -> handleOnDistanceUpdated(event.distance)
            is Event.OnDurationUpdated -> handleOnDurationUpdated(event.duration)
        }
    }


    private fun initData() {
        if (currentState.isInitialized) return
        getDistanceUnits()
        setState { copy(isInitialized = true) }
    }

    private fun getDistanceUnits() {
        viewModelScope.launch {
            val userDetails = sessionManager.getUserDetails()
            val units = userDetails?.distanceUnits ?: DistanceUnits.MILES
            setState {
                copy(
                    selectedDistance = selectedDistance.copy(
                        unit = units,
                    )
                )
            }
        }
    }

    private fun handleBackClick() {
        val state = currentState
        val prevStep = stepManager.getPreviousStep(state)

        when {
            prevStep == null -> setEffect { Effect.NavigateBack }
            prevStep == CreateRunStep.SegmentDetails && state.currentStep == CreateRunStep.SegmentDetails -> setState {
                copy(currentSegmentIndex = currentSegmentIndex - 1)
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

        val nextStep = stepManager.getNextStep(state)

        when {
            nextStep == null -> createEventApi()

            nextStep == CreateRunStep.SegmentDetails && state.currentStep == CreateRunStep.SegmentDetails -> setState {
                copy(currentSegmentIndex = currentSegmentIndex + 1)
            }

            else -> setState { copy(currentStep = nextStep) }
        }
    }

    private fun createEventApi() {

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


}
