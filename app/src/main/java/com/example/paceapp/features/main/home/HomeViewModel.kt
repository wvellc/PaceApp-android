package com.example.paceapp.features.main.home

import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import com.example.paceapp.core.base.BaseViewModel

import com.example.paceapp.features.main.home.HomeContract.Effect
import com.example.paceapp.features.main.home.HomeContract.Event
import com.example.paceapp.features.main.home.HomeContract.State

@HiltViewModel
class HomeViewModel @Inject constructor() : BaseViewModel<State, Event, Effect>() {

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
