package net.paceapp.features.main.createevent

import androidx.compose.foundation.text.input.TextFieldState
import com.wvelabs.core_ui.base.ViewEvent
import com.wvelabs.core_ui.base.ViewSideEffect
import com.wvelabs.core_ui.base.ViewState
import com.wvelabs.core_ui.utils.DateTimeHelper
import kotlinx.datetime.LocalDateTime
import net.paceapp.core.domain.models.DistanceModel
import net.paceapp.core.enums.DistanceUnits
import net.paceapp.features.main.createevent.enums.CreateRunStep
import kotlin.time.Duration.Companion.hours

class CreateEventContract {
    data class State(
        val isInitialized: Boolean = false,
        val isLoading: Boolean = false,
        val currentStep: CreateRunStep = CreateRunStep.EventDetails,
        val isNextEnabled: Boolean = true,
        val eventNameState: TextFieldState = TextFieldState(),
        val locationState: TextFieldState = TextFieldState(),
        val selectedDate: LocalDateTime = DateTimeHelper.now(),
        val selectedDistance: DistanceModel = DistanceModel(1.50f),
        val currentSegmentIndex: Int = 0,
        val goalTimeInSeconds: Long = 1.hours.inWholeSeconds,//1 hour selected by default
        val segmentList: List<String> = emptyList()
    ) : ViewState

    sealed class Event : ViewEvent {
        data object Init : Event()
        data object OnBackClick : Event()
        data object OnNextButtonClick : Event()
        data class OnDateSelected(val millis: Long?) : Event()
        data class OnDistanceUpdated(val distance: DistanceModel) : Event()
        data class OnDurationUpdated(val duration: Long) : Event()
    }

    sealed class Effect : ViewSideEffect {
        data object NavigateBack : Effect()
    }
}
