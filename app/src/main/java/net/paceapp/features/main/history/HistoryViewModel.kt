package net.paceapp.features.main.history

import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.viewModelScope
import com.wvelabs.core_ui.extensions.debounceInput
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import net.paceapp.core.base.BaseViewModel
import net.paceapp.core.mappers.ActivityToUiModelMapper
import net.paceapp.core.models.ActivityDummyData
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
) : BaseViewModel<State, Event, Effect>() {

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


    private fun fetchHistoryList() {
        runTask(
            block = {
                getDummyHistoryList()
            },
            onLoading = { loadingState ->
                setState { copy(isLoading = loadingState) }
            },
            onSuccess = { historyList ->
                setState { copy(historyList = historyList) }
            },
        )
    }


    private fun observeSearchField() {

        val searchFlow = snapshotFlow { currentState.searchTextState.text }
            .debounceInput()
            .map { query ->
                // Execute Use Case
                filterHistoryListUseCase(
                    activities = getDummyHistoryList(),
                    filter = currentState.activeFilter,
                    searchQuery = query.toString(),
                )
            }

        observeState(searchFlow) { filteredList ->
            copy(historyList = filteredList)
        }
    }

    private fun getDummyHistoryList(): List<ActivityUiModel> {
        return ActivityDummyData.getDummyActivities().map { activityToUiModelMapper.map(it) }
    }


    private fun handleOnFilterChange(filter: HistoryFilterModel?) {
        viewModelScope.launch {
            // Get your raw data
            val allActivities = getDummyHistoryList()
            // Pass it to your Use Case (runs safely on background thread)
            val filteredActivities = filterHistoryListUseCase(
                activities = allActivities,
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
        // Sync delete to watch
        history.id.toIntOrNull()?.let { syncId ->
            eventSyncManager.deleteEvent(syncId)
        }
        setState { copy(historyList = historyList - history) }
    }

}
