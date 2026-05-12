package com.example.paceapp.features.main.history

import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.viewModelScope
import com.example.paceapp.core.base.BaseViewModel
import com.example.paceapp.features.main.history.HistoryContract.Effect
import com.example.paceapp.features.main.history.HistoryContract.Event
import com.example.paceapp.features.main.history.HistoryContract.State
import com.example.paceapp.features.main.history.domain.FilterHistoryListUseCase
import com.example.paceapp.features.main.history.models.HistoryFilterModel
import com.example.paceapp.features.main.history.models.HistoryUiModel
import com.wvelabs.core_ui.extensions.debounceInput
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val filterHistoryListUseCase: FilterHistoryListUseCase
) : BaseViewModel<State, Event, Effect>() {

    override fun setInitialState() = State()

    override fun handleEvents(event: Event) {
        when (event) {
            is Event.Init -> initData()
            is Event.OnBackClick -> {
                setEffect { Effect.NavigateBack }
            }

            is Event.OnFilterChange -> handleOnFilterChange(event.filter)
        }
    }

    private fun initData() {
        if (currentState.isInitialized) return
        fetchHistoryList()
        observeSearchField()
        setState { copy(isInitialized = true) }
    }


    private fun fetchHistoryList() {
        safeLaunch(
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

    fun getDummyHistoryList(): List<HistoryUiModel> = listOf(
        HistoryUiModel(
            id = "1",
            title = "Thursday Run",
            date = "29 Jan",
            distance = "5.00 mi",
            time = "0:45",
            avgPace = "9:00 /mi",
            paceDifference = "+01:10",
            isPaceImproved = false
        ), HistoryUiModel(
            id = "2",
            title = "Saturday Run",
            date = "31 Jan",
            distance = "15.00 mi",
            time = "0:50",
            avgPace = "3:20 /mi",
            paceDifference = "-02:15",
            isPaceImproved = true
        ), HistoryUiModel(
            id = "3",
            title = "Saturday Run",
            date = "31 Jan",
            distance = "15.00 mi",
            time = "0:50",
            avgPace = "3:20 /mi",
            paceDifference = "-02:15",
            isPaceImproved = true
        ), HistoryUiModel(
            id = "4",
            title = "Saturday Run",
            date = "31 Jan",
            distance = "15.00 mi",
            time = "0:50",
            avgPace = "3:20 /mi",
            paceDifference = "-02:15",
            isPaceImproved = true
        ), HistoryUiModel(
            id = "5",
            title = "Monday Run",
            date = "02 Feb",
            distance = "3.10 mi",
            time = "0:25",
            avgPace = "8:03 /mi",
            paceDifference = "-00:45",
            isPaceImproved = true
        ), HistoryUiModel(
            id = "6",
            title = "Wednesday Run",
            date = "04 Feb",
            distance = "7.50 mi",
            time = "1:15",
            avgPace = "10:00 /mi",
            paceDifference = "+00:30",
            isPaceImproved = false
        )
    )

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
}
