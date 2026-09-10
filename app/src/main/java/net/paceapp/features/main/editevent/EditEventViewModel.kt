package net.paceapp.features.main.editevent

import androidx.compose.foundation.text.input.TextFieldState
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.wvelabs.core_ui.alerts.MessageType
import com.wvelabs.core_ui.utils.AppDateFormat
import com.wvelabs.core_ui.utils.DateTimeHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import net.paceapp.R
import net.paceapp.core.auth.AuthManager
import net.paceapp.core.base.BaseViewModel
import net.paceapp.core.components.Validator
import net.paceapp.core.components.ValidatorType
import net.paceapp.core.data.firestore.EventDocumentMapper
import net.paceapp.core.data.firestore.EventRepository
import net.paceapp.core.data.firestore.EventStatusValue
import net.paceapp.core.domain.usecases.ValidateEventUseCase
import net.paceapp.core.garmin.EventSyncManager
import net.paceapp.features.main.editevent.EditEventContract.Effect
import net.paceapp.features.main.editevent.EditEventContract.Event
import net.paceapp.features.main.editevent.EditEventContract.State
import net.paceapp.features.main.editevent.navigation.EditEventRoute
import javax.inject.Inject
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.seconds

@HiltViewModel
class EditEventViewModel @Inject constructor(
    private val validateEventUseCase: ValidateEventUseCase,
    private val eventSyncManager: EventSyncManager,
    private val eventRepository: EventRepository,
    private val authManager: AuthManager,
    private val savedStateHandle: SavedStateHandle,
) : BaseViewModel<State, Event, Effect>() {

    override fun setInitialState() = State()

    override fun handleEvents(event: Event) {
        when (event) {
            is Event.Init -> initData()
            is Event.OnBackClick -> {
                setEffect { Effect.NavigateBack }
            }

            is Event.OnSaveButtonClick -> handleSaveButtonClick()
        }
    }


    private fun initData() {
        if (currentState.isInitialized) return

        val args = savedStateHandle.toRoute<EditEventRoute>()
        //Set argument data — keep the original id so save updates in place, and PRE-FILL
        // the editable fields with the current name/location (mirrors iOS EditEvent, which
        // seeds the text fields; previously these values were only shown as grey hints).
        setState {
            copy(
                eventId = args.id.toIntOrNull() ?: 0,
                eventNameState = TextFieldState(args.eventName),
                locationState = TextFieldState(args.location),
                currentEventName = args.eventName,
                currentLocation = args.location,
            )
        }

        setState { copy(isInitialized = true) }
    }


    private fun handleSaveButtonClick() {
        val eventName = currentState.eventNameState.text.trim().toString()
        val location = currentState.locationState.text.trim().toString()
        val error = validateEventUseCase(eventName, location)
        if (error != null) {
            showToast(error, type = MessageType.Warning)
            return
        }

        // Load the authoritative event and override ONLY name/location so distance,
        // goal, segments, etc. are preserved. Keeping the same id makes this an
        // in-place upsert re-sent to the watch + Firestore. Falls back to a
        // minimal payload if the event can't be loaded.
        viewModelScope.launch {
            // Don't write on a session that no longer exists (account deleted elsewhere).
            if (!authManager.verifyAccountStillValid()) return@launch

            val existing = eventRepository.getEvent(currentState.eventId)
            val payload = existing
                ?.let { EventDocumentMapper.connectIQPayload(it).toMutableMap() }
                ?.apply {
                    put("name", eventName)
                    put("location", location)
                }
                ?: buildFallbackPayload(eventName, location)

            // Route by status so the edit persists correctly. createEvent hardcodes
            // isCompleted=false and early-returns without a Firestore write when the id
            // is already completed — a completed-event rename would silently vanish (and
            // could flip it back to active). finishEvent takes the isCompleted=true path.
            if (existing?.status == EventStatusValue.COMPLETED) {
                eventSyncManager.finishEvent(payload)
            } else {
                eventSyncManager.createEvent(payload)
            }
            showToast("$eventName event updated", type = MessageType.Success)
            setEffect { Effect.NavigateBack }
        }
    }

    // Used only when the event can't be loaded from Firestore — keeps the wire
    // shape intact with the same defaults CreateEvent uses.
    private fun buildFallbackPayload(name: String, location: String): Map<String, Any?> {
        val goalSeconds = 1.hours.inWholeSeconds
        val dateStr = DateTimeHelper.formatDateTime(DateTimeHelper.now(), AppDateFormat.DATE_FULL_MDY)
        return mapOf(
            "id" to currentState.eventId,
            "name" to name,
            "location" to location,
            "date" to dateStr,
            "distance" to String.format(java.util.Locale.US, "%.2f", 1.50f),
            "measure" to "Miles",
            "intervals" to "1",
            "goal" to DateTimeHelper.formatDuration(goalSeconds.seconds),
            "activity" to "Run",
            "segmentCount" to 1,
            "segments" to emptyList<Any>(),
            "completedSegments" to emptyList<Any>()
        )
    }

}
