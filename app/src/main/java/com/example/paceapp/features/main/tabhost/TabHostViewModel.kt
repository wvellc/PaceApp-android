package com.example.paceapp.features.main.tabhost

import com.example.paceapp.core.base.BaseViewModel
import com.example.paceapp.features.main.home.models.WatchMetric
import com.example.paceapp.features.main.tabhost.TabHostContract.Effect
import com.example.paceapp.features.main.tabhost.TabHostContract.Event
import com.example.paceapp.features.main.tabhost.TabHostContract.State
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class TabHostViewModel @Inject constructor() : BaseViewModel<State, Event, Effect>() {

    override fun setInitialState() = State()

    override fun handleEvents(event: Event) {
        when (event) {
            is Event.Init -> initData()
        }
    }

    private fun initData() {
        if (currentState.isInitialized) return


        setState { copy(isInitialized = true, metrics = getWatchMetrics()) }
    }

    private fun getWatchMetrics(
        bpm: String = "60",
        hrs: String = "12",
        goal: String = "-01:10",
        left: String = "07:20",
        pace: String = "9:09"
    ): List<WatchMetric> = listOf(
        WatchMetric.HeartRate(bpm),
        WatchMetric.OverallTime(hrs),
        WatchMetric.GoalTime(goal),
        WatchMetric.RemainingTime(left),
        WatchMetric.Pace(pace)
    )
}
