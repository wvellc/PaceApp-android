package com.example.paceapp.features.main.createevent

import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import com.example.paceapp.core.base.BaseViewModel

import com.example.paceapp.features.main.createevent.CreateEventContract.Effect
import com.example.paceapp.features.main.createevent.CreateEventContract.Event
import com.example.paceapp.features.main.createevent.CreateEventContract.State

@HiltViewModel
class CreateEventViewModel @Inject constructor() : BaseViewModel<State, Event, Effect>() {

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
