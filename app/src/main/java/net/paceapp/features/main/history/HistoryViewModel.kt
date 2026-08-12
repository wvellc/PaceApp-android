package net.paceapp.features.main.history

import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.viewModelScope
import com.wvelabs.core_ui.extensions.debounceInput
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import net.paceapp.core.auth.AuthManager
import net.paceapp.core.base.BaseViewModel
import net.paceapp.core.data.firestore.EventRepository
import net.paceapp.core.mappers.ActivityToUiModelMapper
import net.paceapp.core.mappers.EventDocumentUiMapper
import net.paceapp.core.models.ActivityUiModel
import net.paceapp.features.main.history.HistoryContract.Effect
import net.paceapp.features.main.history.HistoryContract.Event
import net.paceapp.features.main.history.HistoryContract.State
import net.paceapp.features.main.history.domain.FilterHistoryListUseCase
import net.paceapp.core.garmin.EventSyncManager
import net.paceapp.features.main.history.models.HistoryFilterModel
import javax.inject.Inject

@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val filterHistoryListUseCase: FilterHistoryListUseCase,
    private val activityToUiModelMapper: ActivityToUiModelMapper,
    private val eventSyncManager: EventSyncManager,
    private val eventRepository: EventRepository,
    private val authManager: AuthManager,
) : BaseViewModel<State, Event, Effect>() {

    // Latest completed events from Firestore (newest-first), mapped to UI models.
    // Held so filter/search can operate against the real source without re-fetching.
    private var completedActivities: List<ActivityUiModel> = emptyList()

    override fun setInitialState() = State()

    override fun handleEvents(event: Event) {

        when (event) {
            is Event.Init -> initData()
            is Event.OnBackClick -> {
                setEffect { Effect.NavigateBack }
            }

            is Event.OnFilterChange -> handleOnFilterChange(event.filter)
            is Event.OnHistoryClick -> handleOnHistoryClick(event.history)
            is Event.OnDuplicateHistoryClick -> handleOnDuplicateHistoryClick(event.history)
            is Event.OnDeleteHistoryClick -> handleOnDeleteHistoryClick(event.history)
        }
    }


    private fun initData() {
        if (currentState.isInitialized) return
        fetchHistoryList()
        observeSearchField()
        setState { copy(isInitialized = true) }
    }


    // Completed events come from Firestore (shared thepaceapp backend), ordered
    // newest-first. On every emission we cache the raw list and re-apply the
    // currently active filter/search so live updates respect user filtering.
    private fun fetchHistoryList() {
        val uid = authManager.currentUid ?: return
        eventRepository.observeCompletedEvents(uid)
            .onEach { docs ->
                completedActivities = docs
                    .map { activityToUiModelMapper.map(EventDocumentUiMapper.toActivityModel(it)) }
                val filtered = filterHistoryListUseCase(
                    activities = completedActivities,
                    filter = currentState.activeFilter,
                    searchQuery = currentState.searchTextState.text.toString(),
                )
                setState { copy(historyList = filtered, isLoading = false) }
            }
            .launchIn(viewModelScope)
    }


    private fun observeSearchField() {

        val searchFlow = snapshotFlow { currentState.searchTextState.text }
            .debounceInput()
            .map { query ->
                // Filter the cached Firestore list against the current query/filter
                filterHistoryListUseCase(
                    activities = completedActivities,
                    filter = currentState.activeFilter,
                    searchQuery = query.toString(),
                )
            }

        observeState(searchFlow) { filteredList ->
            copy(historyList = filteredList)
        }
    }


    private fun handleOnFilterChange(filter: HistoryFilterModel?) {
        viewModelScope.launch {
            // Filter the cached Firestore list (runs safely on background thread)
            val filteredActivities = filterHistoryListUseCase(
                activities = completedActivities,
                filter = filter,
                searchQuery = currentState.searchTextState.text.toString(),
            )
            // Update the UI state with the result
            setState {
                copy(
                    activeFilter = filter,
                    historyList = filteredActivities
                )
            }
        }
    }

    private fun handleOnHistoryClick(history: ActivityUiModel) {
        setEffect {
            Effect.NavigateToEventDetails(
                id = history.id,
                eventName = history.title,
                location = history.location,
                date = history.date
            )
        }
    }

    private fun handleOnDuplicateHistoryClick(history: ActivityUiModel) {
        setEffect {
            Effect.NavigateToDuplicateEvent(
                id = history.id,
                eventName = history.title,
                location = history.location,
                date = history.date
            )
        }

    }

    private fun handleOnDeleteHistoryClick(history: ActivityUiModel) {
        viewModelScope.launch {
            // Don't delete on a session that no longer exists (account deleted elsewhere).
            if (!authManager.verifyAccountStillValid()) return@launch
            // Sync delete to watch
            history.id.toIntOrNull()?.let { syncId ->
                eventSyncManager.deleteEvent(syncId)
            }
            setState { copy(historyList = historyList - history) }
        }
    }

}
