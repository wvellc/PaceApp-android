package com.example.paceapp.features.authentication.buildprofile

import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import com.example.paceapp.core.base.BaseViewModel

import com.example.paceapp.features.authentication.buildprofile.BuildProfileContract.Effect
import com.example.paceapp.features.authentication.buildprofile.BuildProfileContract.Event
import com.example.paceapp.features.authentication.buildprofile.BuildProfileContract.State

@HiltViewModel
class BuildProfileViewModel @Inject constructor() : BaseViewModel<State, Event, Effect>() {

    override fun setInitialState() = State()

    override fun handleEvents(event: Event) {
        when (event) {
            is Event.Init -> initData()
        }
    }

    private fun initData() {
        if (currentState.isInitialized) return

        // TODO: Initialize Data

        setState { copy(isInitialized = true) }
    }
}
