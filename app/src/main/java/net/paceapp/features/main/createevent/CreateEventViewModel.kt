package net.paceapp.features.main.createevent

import androidx.lifecycle.viewModelScope
import com.wvelabs.core_ui.utils.DateTimeHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import net.paceapp.core.base.BaseViewModel
import net.paceapp.core.enums.DistanceUnits
import net.paceapp.features.main.createevent.CreateEventContract.Effect
import net.paceapp.features.main.createevent.CreateEventContract.Event
import net.paceapp.features.main.createevent.CreateEventContract.State
import net.paceapp.features.main.createevent.enums.CreateRunStep
import net.paceapp.session.AppSessionManager
import javax.inject.Inject

@HiltViewModel
class CreateEventViewModel @Inject constructor(
    private val sessionManager: AppSessionManager
) : BaseViewModel<State, Event, Effect>() {
    private val stepList by lazy { CreateRunStep.entries.toMutableList() }

    override fun setInitialState() = State()

    override fun handleEvents(event: Event) {
        when (event) {
            is Event.Init -> initData()
            is Event.OnBackClick -> handleBackClick()
            is Event.OnNextButtonClick -> handleNextButtonClick()
            is Event.OnDateSelected -> handleDateSelected(event.millis)
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
            setState { copy(distanceUnits = units) }
        }
    }

    private fun handleBackClick() {

        when (currentState.currentStep.ordinal) {
            0 -> setEffect { Effect.NavigateBack }
            else -> {
                if (currentState.currentStep == CreateRunStep.SegmentDetails) {

                }
                val prevIndex = currentState.currentStep.ordinal - 1
                setState { copy(currentStep = stepList[prevIndex]) }
            }
        }
    }

    private fun handleNextButtonClick() {
        when (currentState.currentStep.ordinal) {
            stepList.lastIndex -> createEventApi()

            else -> {
                if (currentState.currentStep == CreateRunStep.SegmentDetails) {

                }
                val nextIndex = currentState.currentStep.ordinal + 1
                setState { copy(currentStep = stepList[nextIndex]) }
            }
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

}
