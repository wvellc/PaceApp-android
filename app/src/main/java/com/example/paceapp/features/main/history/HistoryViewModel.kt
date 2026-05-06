package com.example.paceapp.features.main.history

import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import com.example.paceapp.core.base.BaseViewModel

import com.example.paceapp.features.main.history.HistoryContract.Effect
import com.example.paceapp.features.main.history.HistoryContract.Event
import com.example.paceapp.features.main.history.HistoryContract.State

@HiltViewModel
class HistoryViewModel @Inject constructor() : BaseViewModel<State, Event, Effect>() {

    override fun setInitialState() = State()

    override fun handleEvents(event: Event) {
        when (event) {
            is Event.Init -> initData()
            is Event.OnBackClick -> {
                 setEffect { Effect.NavigateBack }
            }
        }
    }

    private fun initData() {
        if (currentState.isInitialized) return

        // TODO: Initialize Data

        setState { copy(isInitialized = true) }
    }
}
