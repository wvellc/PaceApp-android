package net.paceapp.features.main.createevent

import androidx.compose.foundation.text.input.TextFieldState
import com.wvelabs.core_ui.base.ViewEvent
import com.wvelabs.core_ui.base.ViewSideEffect
import com.wvelabs.core_ui.base.ViewState
import com.wvelabs.core_ui.utils.DateTimeHelper
import kotlinx.datetime.LocalDateTime
import net.paceapp.core.domain.models.DistanceModel
import net.paceapp.features.main.createevent.enums.EventType
import net.paceapp.features.main.createevent.enums.CreateRunStep
import net.paceapp.features.main.createevent.models.RunSegment
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
        val goalTimeInSeconds: Long = 1.hours.inWholeSeconds,//1 hour selected by default
        val hasSegments: Boolean = false,
        val segmentCount: Int = 3,
        val currentSegmentIndex: Int = 0,
        val segmentList: List<RunSegment> = emptyList(),
        val segmentError: String? = null,
        val lookBackInterval: Int = 1,
        val eventType: EventType = EventType.Run,
    ) : ViewState

    sealed class Event : ViewEvent {
        data object Init : Event()
        data object OnBackClick : Event()
        data object OnNextButtonClick : Event()
        data class OnDateSelected(val millis: Long?) : Event()
        data class OnDistanceUpdated(val distance: DistanceModel) : Event()
        data class OnDurationUpdated(val duration: Long) : Event()
        data class OnSegmentChoiceUpdated(val hasSegments: Boolean) : Event()
        data class OnSegmentCountChange(val count: Int) : Event()
        data class OnSegmentUpdated(val segment: RunSegment) : Event()
        data class OnLookBackIntervalUpdated(val interval: Int) : Event()
        data class OnEventTypeUpdated(val type: EventType) : Event()
        data object OnStepInfoClick : Event()
    }

    sealed class Effect : ViewSideEffect {
        data object NavigateBack : Effect()
    }
}
