package net.paceapp.features.main.eventdetails

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import net.paceapp.core.base.BaseViewModel
import net.paceapp.core.enums.DistanceUnits
import net.paceapp.features.main.eventdetails.EventDetailsContract.Effect
import net.paceapp.features.main.eventdetails.EventDetailsContract.Event
import net.paceapp.features.main.eventdetails.EventDetailsContract.State
import net.paceapp.features.main.eventdetails.models.EventDummyData
import net.paceapp.session.AppSessionManager
import javax.inject.Inject

@HiltViewModel
class EventDetailsViewModel @Inject constructor(

    private val appSession: AppSessionManager
) : BaseViewModel<State, Event, Effect>() {

    override fun setInitialState() = State()

    override fun handleEvents(event: Event) {
        when (event) {
            is Event.Init -> initData()
            is Event.OnBackClick -> {
                setEffect { Effect.NavigateBack }
            }

            is Event.OnFavoriteToggle -> handleOnFavoriteToggle()
            is Event.OnAnalyticsToggle -> handleOnAnalyticsToggle()
            is Event.OnIntervalsToggle -> handleOnIntervalsToggle()
            is Event.OnSegmentsToggle -> handleOnSegmentsToggle()
        }
    }


    private fun initData() {
        if (currentState.isInitialized) return
        fetchEventData()
        setState { copy(isInitialized = true) }
    }

    private fun fetchEventData() {
        viewModelScope.launch {
            setState { copy(isLoading = true) }
            //TODO:Replace With Firebase or API call
            val defaultUnit = appSession.getUserDetails()?.distanceUnits ?: DistanceUnits.MILES
            delay(800)//Dummy loading
            val eventData = EventDummyData.getMockEvent(defaultUnit)
            setState { copy(eventDetails = eventData, isLoading = false) }
        }
    }


    private fun handleOnFavoriteToggle() {
        setState { copy(isFavorite = isFavorite.not()) }
    }

    private fun handleOnAnalyticsToggle() {
        setState { copy(isAnalyticsExpanded = isAnalyticsExpanded.not()) }

    }

    private fun handleOnIntervalsToggle() {
        setState { copy(isIntervalsExpanded = isIntervalsExpanded.not()) }

    }

    private fun handleOnSegmentsToggle() {
        setState { copy(isSegmentsExpanded = isSegmentsExpanded.not()) }
    }
}
