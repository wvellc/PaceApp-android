package net.paceapp.features.main.eventdetails

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.wvelabs.core_ui.alerts.MessageType
import com.wvelabs.core_ui.utils.AppDateFormat
import com.wvelabs.core_ui.utils.DateTimeHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import net.paceapp.core.base.BaseViewModel
import net.paceapp.core.enums.DistanceUnits
import net.paceapp.features.main.eventdetails.EventDetailsContract.Effect
import net.paceapp.features.main.eventdetails.EventDetailsContract.Event
import net.paceapp.features.main.eventdetails.EventDetailsContract.State
import net.paceapp.features.main.eventdetails.models.EventDummyData
import net.paceapp.features.main.eventdetails.navigation.EventDetailsRoute
import net.paceapp.core.garmin.EventSyncManager
import net.paceapp.session.AppSessionManager
import javax.inject.Inject

@HiltViewModel
class EventDetailsViewModel @Inject constructor(
    private val appSession: AppSessionManager,
    private val savedStateHandle: SavedStateHandle,
    private val eventSyncManager: EventSyncManager,
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
            is Event.OnEditButtonClick -> handleOnEditButtonClick()
            is Event.OnDeleteButtonClick -> handleOnDeleteButtonClick()
            is Event.OnDeleteEventConfirmation -> handleOnDeleteEventConfirmation()
            is Event.OnMapClick -> handleOnMapClick()
        }
    }




    private fun initData() {
        if (currentState.isInitialized) return
        val args = savedStateHandle.toRoute<EventDetailsRoute>()
        fetchEventData(args.id, args.eventName, args.location, args.date)
        setState { copy(isInitialized = true) }
    }

    private fun fetchEventData(
        id: String,
        eventName: String,
        location: String,
        date: String
    ) {
        val formattedDate = DateTimeHelper.getDateTime(
            date = date,
            format = AppDateFormat.DATE_SHORT_DM,
            isUtc = true
        )
        viewModelScope.launch {
            setState { copy(isLoading = true) }
            //TODO:Replace With Firebase or API call
            val defaultUnit = appSession.getUserDetails()?.distanceUnits ?: DistanceUnits.MILES
            delay(800)//Dummy loading
            val eventData = EventDummyData.getMockEvent(defaultUnit).copy(
                id = id,
                title = eventName,
                location = location,
                dateTime = formattedDate ?: DateTimeHelper.now()

            )
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

    private fun handleOnEditButtonClick() {
        val event = currentState.eventDetails
        if (event == null) return
        setEffect {
            Effect.NavigateToEditEvent(
                id = event.id,
                eventName = event.title,
                location = event.location,
            )
        }
    }

    private fun handleOnDeleteButtonClick() {
        setEffect { Effect.ShowDeleteEventDialog }
    }

    private fun handleOnDeleteEventConfirmation() {
        val event = currentState.eventDetails
        if (event != null) {
            // Sync delete to watch
            event.id.toIntOrNull()?.let { syncId ->
                eventSyncManager.deleteEvent(syncId)
            }
        }
        showToast("Event deleted successfully", type = MessageType.Success)
        setEffect { Effect.NavigateBack }
    }

    private fun handleOnMapClick() {
        setEffect { Effect.NavigateToEventMap }
    }
}
