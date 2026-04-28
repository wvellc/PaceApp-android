package com.example.paceapp.features.authentication.buildprofile

import com.example.paceapp.core.base.BaseViewModel
import com.example.paceapp.features.authentication.buildprofile.BuildProfileContract.Effect
import com.example.paceapp.features.authentication.buildprofile.BuildProfileContract.Event
import com.example.paceapp.features.authentication.buildprofile.BuildProfileContract.State
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class BuildProfileViewModel @Inject constructor() : BaseViewModel<State, Event, Effect>() {

    override fun setInitialState() = State()

    override fun handleEvents(event: Event) {
        when (event) {
            is Event.Init -> initData()
            is Event.OnBackClick -> {
                setEffect { Effect.NavigateBack }
            }
            is Event.OnNextClick -> handleNextButtonClicked()
        }
    }

    private fun initData() {
        if (currentState.isInitialized) return



        setState { copy(isInitialized = true) }
    }


    private fun handleNextButtonClicked() {

    }
}
