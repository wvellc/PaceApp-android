package net.paceapp.features.main.eventdetails

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.wvelabs.core_ui.alerts.MessageType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import net.paceapp.core.auth.AuthManager
import net.paceapp.core.base.BaseViewModel
import net.paceapp.core.data.firestore.EventRepository
import net.paceapp.core.data.firestore.FavoriteRepository
import net.paceapp.features.main.eventdetails.EventDetailsContract.Effect
import net.paceapp.features.main.eventdetails.EventDetailsContract.Event
import net.paceapp.features.main.eventdetails.EventDetailsContract.State
import net.paceapp.features.main.eventdetails.mappers.EventDetailsMapper
import net.paceapp.features.main.eventdetails.navigation.EventDetailsRoute
import net.paceapp.core.garmin.EventSyncManager
import javax.inject.Inject

@HiltViewModel
class EventDetailsViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val eventSyncManager: EventSyncManager,
    private val eventRepository: EventRepository,
    private val favoriteRepository: FavoriteRepository,
    private val authManager: AuthManager,
) : BaseViewModel<State, Event, Effect>() {

    // Firestore document id of the event being viewed (String(id)) — used for favorites.
    private var eventDocId: String? = null

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
        eventDocId = args.id
        setState { copy(isInitialized = true) }
        fetchEventData(args.id)
        fetchFavoriteStatus()
    }

    // Initial favorite state from Firestore (mirrors iOS fetchInitialFavoriteStatus).
    private fun fetchFavoriteStatus() {
        val uid = authManager.currentUid ?: return
        val eventId = eventDocId ?: return
        viewModelScope.launch {
            val favorited = runCatching { favoriteRepository.isFavorited(uid, eventId) }.getOrDefault(false)
            setState { copy(isFavorite = favorited) }
        }
    }

    // Loads the single event from Firestore (shared thepaceapp backend) and maps it
    // to the UI model. The nav arg id is the Firestore document key (String(id)).
    private fun fetchEventData(id: String) {
        val eventId = id.toIntOrNull()
        if (eventId == null || authManager.currentUid == null) {
            setState { copy(eventDetails = null, isLoading = false) }
            return
        }
        viewModelScope.launch {
            setState { copy(isLoading = true) }
            val document = eventRepository.getEvent(eventId)
            val eventData = document?.let { EventDetailsMapper.toUiModel(it) }
            setState { copy(eventDetails = eventData, isLoading = false) }
        }
    }


    // Optimistic toggle, then reconcile with the server result (rollback on failure).
    // Mirrors iOS EventDetailsViewModel.toggleFavorite().
    private fun handleOnFavoriteToggle() {
        val uid = authManager.currentUid ?: return
        val eventId = eventDocId ?: return
        val previous = currentState.isFavorite
        setState { copy(isFavorite = !previous) }
        viewModelScope.launch {
            runCatching { favoriteRepository.toggleFavorite(uid, eventId) }
                .onSuccess { newState -> setState { copy(isFavorite = newState) } }
                .onFailure { setState { copy(isFavorite = previous) } }
        }
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
