package com.example.paceapp.features.main.history

import com.example.paceapp.core.base.BaseViewModel
import com.example.paceapp.features.main.history.HistoryContract.Effect
import com.example.paceapp.features.main.history.HistoryContract.Event
import com.example.paceapp.features.main.history.HistoryContract.State
import com.example.paceapp.features.main.history.domain.HistoryUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class HistoryViewModel @Inject constructor() : BaseViewModel<State, Event, Effect>() {

    override fun setInitialState() = State()

    override fun handleEvents(event: Event) {
        when (event) {
            is Event.Init -> initData()
            is Event.OnBackClick -> {
                setEffect { Effect.NavigateBack }
            }

            is Event.OnFilterClick -> handleOnFilterClick()
        }
    }

    private fun initData() {
        if (currentState.isInitialized) return
        setState { copy(historyList = getDummyRunActivities()) }
        setState { copy(isInitialized = true) }
    }

    fun getDummyRunActivities(): List<HistoryUiModel> = listOf(
        HistoryUiModel(
            id = "1",
            title = "Thursday Run",
            date = "29 Jan",
            distance = "5.00 mi",
            time = "0:45",
            avgPace = "9:00 /mi",
            paceDifference = "+01:10",
            isPaceImproved = false
        ),
        HistoryUiModel(
            id = "2",
            title = "Saturday Run",
            date = "31 Jan",
            distance = "15.00 mi",
            time = "0:50",
            avgPace = "3:20 /mi",
            paceDifference = "-02:15",
            isPaceImproved = true
        ),
        HistoryUiModel(
            id = "3",
            title = "Saturday Run",
            date = "31 Jan",
            distance = "15.00 mi",
            time = "0:50",
            avgPace = "3:20 /mi",
            paceDifference = "-02:15",
            isPaceImproved = true
        ),
        HistoryUiModel(
            id = "4",
            title = "Saturday Run",
            date = "31 Jan",
            distance = "15.00 mi",
            time = "0:50",
            avgPace = "3:20 /mi",
            paceDifference = "-02:15",
            isPaceImproved = true
        ),
        HistoryUiModel(
            id = "5",
            title = "Monday Run",
            date = "02 Feb",
            distance = "3.10 mi",
            time = "0:25",
            avgPace = "8:03 /mi",
            paceDifference = "-00:45",
            isPaceImproved = true
        ),
        HistoryUiModel(
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

    private fun handleOnFilterClick() {
        //  TODO ("Not yet implemented")
    }
}
