package com.example.paceapp.features.main.createevent

import androidx.compose.foundation.text.input.TextFieldState
import com.example.paceapp.core.enums.DistanceUnits
import com.example.paceapp.features.main.createevent.enums.CreateRunStep
import com.wvelabs.core_ui.base.ViewEvent
import com.wvelabs.core_ui.base.ViewSideEffect
import com.wvelabs.core_ui.base.ViewState
import kotlinx.datetime.LocalDateTime

class CreateEventContract {
    data class State(
        val isInitialized: Boolean = false,
        val isLoading: Boolean = false,
        val currentStep: CreateRunStep = CreateRunStep.EventDetails,
        val isNextEnabled: Boolean = true,
        val distanceUnits: DistanceUnits = DistanceUnits.MILES,
        val eventNameState: TextFieldState = TextFieldState(),
        val locationState: TextFieldState = TextFieldState(),
        val selectedDate : LocalDateTime? = null,
        val formatedDate : String? = null,
    ) : ViewState

    sealed class Event : ViewEvent {

        data object Init : Event()
        data object OnBackClick : Event()
        data class OnDateSelected(val millis: Long?) : Event()

    }

    sealed class Effect : ViewSideEffect {
        data object NavigateBack : Effect()
    }
}
