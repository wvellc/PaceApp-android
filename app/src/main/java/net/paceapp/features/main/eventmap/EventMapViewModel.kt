package net.paceapp.features.main.eventmap

import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.lifecycle.HiltViewModel
import net.paceapp.core.base.BaseViewModel
import net.paceapp.features.main.eventdetails.models.EventDummyData
import net.paceapp.features.main.eventmap.EventMapContract.Effect
import net.paceapp.features.main.eventmap.EventMapContract.Event
import net.paceapp.features.main.eventmap.EventMapContract.State
import javax.inject.Inject

@HiltViewModel
class EventMapViewModel @Inject constructor() : BaseViewModel<State, Event, Effect>() {

    override fun setInitialState() = State()

    override fun handleEvents(event: Event) {
        when (event) {
            is Event.Init -> initData()
            is Event.OnBackClick -> {
                setEffect { Effect.NavigateBack }
            }

            is Event.OnMapLoaded -> handleMapLoaded()
            is Event.OnMarkerClicked -> handleMarkerClicked(event.position)
        }
    }


    private fun initData() {
        if (currentState.isInitialized) return
        setState { copy(isInitialized = true) }
    }

    private fun fetchMapData() {
        val points = EventDummyData.mockCoordinates.map {
            LatLng(it.latitude, it.longitude)
        }
        val centerLat = points.map { it.latitude }.average()
        val centerLng = points.map { it.longitude }.average()

        val centerPoint = LatLng(centerLat, centerLng)
        setState { copy(routePoints = points, startLocation = centerPoint) }
    }


    private fun handleMapLoaded() {
        fetchMapData()
    }

    private fun handleMarkerClicked(position: LatLng) {

    }

}
