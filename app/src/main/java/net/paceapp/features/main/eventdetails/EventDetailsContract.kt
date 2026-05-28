package net.paceapp.features.main.eventdetails

import com.wvelabs.core_ui.base.ViewEvent
import com.wvelabs.core_ui.base.ViewSideEffect
import com.wvelabs.core_ui.base.ViewState

class EventDetailsContract {
    data class State(
        val isInitialized: Boolean = false,
        val isLoading: Boolean = false,
        val isFavorite: Boolean = false,
    ) : ViewState

    sealed class Event : ViewEvent {
        data object Init : Event()
        data object OnBackClick : Event()
        data object OnFavoriteToggle : Event()
    }

    sealed class Effect : ViewSideEffect {
        data object NavigateBack : Effect()
    }
}
