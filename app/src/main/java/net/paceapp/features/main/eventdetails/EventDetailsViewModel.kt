package net.paceapp.features.main.eventdetails

import dagger.hilt.android.lifecycle.HiltViewModel
import net.paceapp.core.base.BaseViewModel
import net.paceapp.features.main.eventdetails.EventDetailsContract.Effect
import net.paceapp.features.main.eventdetails.EventDetailsContract.Event
import net.paceapp.features.main.eventdetails.EventDetailsContract.State
import javax.inject.Inject

@HiltViewModel
class EventDetailsViewModel @Inject constructor() : BaseViewModel<State, Event, Effect>() {

    override fun setInitialState() = State()

    override fun handleEvents(event: Event) {
        when (event) {
            is Event.Init -> initData()
            is Event.OnBackClick -> {
                setEffect { Effect.NavigateBack }
            }

            is Event.OnFavoriteToggle -> handleOnFavoriteToggle()
        }
    }


    private fun initData() {
        if (currentState.isInitialized) return
        setState { copy(isInitialized = true) }
    }


    private fun handleOnFavoriteToggle() {
        setState { copy(isFavorite = isFavorite.not()) }
    }
}
