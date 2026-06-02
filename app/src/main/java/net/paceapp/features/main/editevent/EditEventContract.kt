package net.paceapp.features.main.editevent

import androidx.compose.foundation.text.input.TextFieldState
import com.wvelabs.core_ui.base.ViewEvent
import com.wvelabs.core_ui.base.ViewSideEffect
import com.wvelabs.core_ui.base.ViewState

class EditEventContract {
    data class State(
        val isInitialized: Boolean = false,
        val isLoading: Boolean = false,
        val eventNameState: TextFieldState = TextFieldState(),
        val locationState: TextFieldState = TextFieldState(),
        val currentEventName: String? = null,
        val currentLocation: String? = null,
    ) : ViewState

    sealed class Event : ViewEvent {
        data object Init : Event()
        data object OnBackClick : Event()
       data object OnSaveButtonClick : Event()
    }

    sealed class Effect : ViewSideEffect {
        data object NavigateBack : Effect()
    }
}
