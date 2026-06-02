package net.paceapp.features.main.eventmap

import com.google.android.gms.maps.model.LatLng
import com.wvelabs.core_ui.base.ViewEvent
import com.wvelabs.core_ui.base.ViewSideEffect
import com.wvelabs.core_ui.base.ViewState

class EventMapContract {
    data class State(
        val isInitialized: Boolean = false,
        val isLoading: Boolean = false,
        val startLocation: LatLng? = null,
        val routePoints: List<LatLng> = emptyList(),
    ) : ViewState

    sealed class Event : ViewEvent {
        data object Init : Event()
        data object OnBackClick : Event()
        data object OnMapLoaded : Event()
        data class OnMarkerClicked(val position: LatLng) : Event()
    }

    sealed class Effect : ViewSideEffect {
        data object NavigateBack : Effect()
    }
}
