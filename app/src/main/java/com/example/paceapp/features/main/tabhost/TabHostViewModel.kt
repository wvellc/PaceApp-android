package com.example.paceapp.features.main.tabhost

import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import com.example.paceapp.core.base.BaseViewModel

import com.example.paceapp.features.main.tabhost.TabHostContract.Effect
import com.example.paceapp.features.main.tabhost.TabHostContract.Event
import com.example.paceapp.features.main.tabhost.TabHostContract.State

@HiltViewModel
class TabHostViewModel @Inject constructor() : BaseViewModel<State, Event, Effect>() {

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
