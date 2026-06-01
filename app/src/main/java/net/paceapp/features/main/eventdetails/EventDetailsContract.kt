package net.paceapp.features.main.eventdetails

import com.wvelabs.core_ui.base.ViewEvent
import com.wvelabs.core_ui.base.ViewSideEffect
import com.wvelabs.core_ui.base.ViewState
import net.paceapp.features.main.eventdetails.models.EventDetailsUiModel

class EventDetailsContract {
    data class State(
        val isInitialized: Boolean = false,
        val isLoading: Boolean = false,
        val isFavorite: Boolean = false,
        val eventDetails: EventDetailsUiModel? = null,
        val isAnalyticsExpanded: Boolean = true,
        val isIntervalsExpanded: Boolean = false,
        val isSegmentsExpanded: Boolean = false,
    ) : ViewState

    sealed class Event : ViewEvent {
        data object Init : Event()
        data object OnBackClick : Event()
        data object OnFavoriteToggle : Event()
        data object OnAnalyticsToggle : Event()
        data object OnIntervalsToggle : Event()
        data object OnSegmentsToggle : Event()
    }

    sealed class Effect : ViewSideEffect {
        data object NavigateBack : Effect()
    }
}
