package com.example.paceapp.features.authentication.profilecreated

import com.example.paceapp.core.base.BaseViewModel
import com.example.paceapp.features.authentication.profilecreated.ProfileCreatedContract.Effect
import com.example.paceapp.features.authentication.profilecreated.ProfileCreatedContract.Event
import com.example.paceapp.features.authentication.profilecreated.ProfileCreatedContract.State
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ProfileCreatedViewModel @Inject constructor() : BaseViewModel<State, Event, Effect>() {

    override fun setInitialState() = State()

    override fun handleEvents(event: Event) {
        when (event) {
            is Event.Init -> initData()
            is Event.OnBackClick -> {
                setEffect { Effect.NavigateBack }
            }
            is Event.OnGetStartedClicked -> handleGetStartedClicked()
        }
    }


    private fun initData() {
        if (currentState.isInitialized) return
        setState { copy(isInitialized = true) }
    }

    private fun handleGetStartedClicked() {
        setEffect { Effect.NavigateToTabHost }
    }
}
