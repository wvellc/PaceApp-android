package net.paceapp.features.main.duplicateevent

import androidx.compose.foundation.text.input.TextFieldState
import com.wvelabs.core_ui.base.ViewEvent
import com.wvelabs.core_ui.base.ViewSideEffect
import com.wvelabs.core_ui.base.ViewState
import com.wvelabs.core_ui.utils.DateTimeHelper
import kotlinx.datetime.LocalDateTime

class DuplicateEventContract {
    data class State(
        val isInitialized: Boolean = false,
        val isLoading: Boolean = false,
        val eventNameState: TextFieldState = TextFieldState(),
        val locationState: TextFieldState = TextFieldState(),
        val selectedDate: LocalDateTime = DateTimeHelper.now(),
    ) : ViewState

    sealed class Event : ViewEvent {
        data object Init : Event()
        data object OnBackClick : Event()
        data class OnDateSelected(val millis: Long?) : Event()
        data object OnSaveButtonClick : Event()
    }

    sealed class Effect : ViewSideEffect {
        data object NavigateBack : Effect()
    }
}
