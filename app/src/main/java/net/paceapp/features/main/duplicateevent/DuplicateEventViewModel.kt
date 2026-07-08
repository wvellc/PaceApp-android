package net.paceapp.features.main.duplicateevent

import androidx.compose.foundation.text.input.TextFieldState
import androidx.lifecycle.SavedStateHandle
import androidx.navigation.toRoute
import com.wvelabs.core_ui.alerts.MessageType
import com.wvelabs.core_ui.utils.AppDateFormat
import com.wvelabs.core_ui.utils.DateTimeHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import net.paceapp.core.base.BaseViewModel
import net.paceapp.core.domain.usecases.ValidateEventUseCase
import net.paceapp.core.garmin.EventSyncManager
import net.paceapp.features.main.duplicateevent.DuplicateEventContract.Effect
import net.paceapp.features.main.duplicateevent.DuplicateEventContract.Event
import net.paceapp.features.main.duplicateevent.DuplicateEventContract.State
import net.paceapp.features.main.duplicateevent.navigation.DuplicateEventRoute
import java.util.Locale
import javax.inject.Inject
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.seconds

@HiltViewModel
class DuplicateEventViewModel @Inject constructor(
    private val validateEventUseCase: ValidateEventUseCase,
    private val eventSyncManager: EventSyncManager,
    private val saveStateHandle: SavedStateHandle,
) : BaseViewModel<State, Event, Effect>() {

    override fun setInitialState() = State()

    override fun handleEvents(event: Event) {
        when (event) {
            is Event.Init -> initData()
            is Event.OnBackClick -> {
                setEffect { Effect.NavigateBack }
            }

            is Event.OnDateSelected -> handleDateSelected(event.millis)
            is Event.OnSaveButtonClick -> handleSaveButtonClick()
        }
    }


    private fun initData() {
        if (currentState.isInitialized) return

        val args = saveStateHandle.toRoute<DuplicateEventRoute>()

        val formattedDate = DateTimeHelper.getDateTime(
            date = args.date,
            format = AppDateFormat.DATE_SHORT_DM,
            isUtc = true
        )
        setState {
            copy(
                isInitialized = true,
                eventNameState = TextFieldState(args.eventName),
                locationState = TextFieldState(args.location),
                selectedDate = formattedDate ?: DateTimeHelper.now(),
            )
        }
    }

    private fun handleDateSelected(millis: Long?) {
        setState {
            copy(
                selectedDate = DateTimeHelper.getUTCLocalDateTime(millis ?: 0L),
            )
        }
    }

    private fun handleSaveButtonClick() {
        val eventName = currentState.eventNameState.text.trim().toString()
        val location = currentState.locationState.text.trim().toString()
        val error = validateEventUseCase(eventName, location)
        if (error != null) {
            showToast(error, type = MessageType.Warning)
            return
        }

        // New id → a brand new active event, then sync to watch/Firestore.
        val payload = buildEventPayload(eventName, location)
        eventSyncManager.createEvent(payload)

        showToast("Successfully created", type = MessageType.Success)
        setEffect { Effect.NavigateBack }
    }

    // Wire payload in Garmin format with a NEW id and the user's edited fields.
    // Fields not editable on this screen use the same defaults CreateEvent uses.
    private fun buildEventPayload(name: String, location: String): Map<String, Any?> {
        val goalSeconds = 1.hours.inWholeSeconds
        val dateStr = DateTimeHelper.formatDateTime(currentState.selectedDate, AppDateFormat.DATE_FULL_MDY)
        return mapOf(
            "id" to (System.currentTimeMillis() / 1000).toInt(),
            "name" to name,
            "location" to location,
            "date" to dateStr,
            "distance" to String.format(Locale.getDefault(), "%.2f", 1.50f),
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
