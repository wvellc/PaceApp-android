package com.example.paceapp.features.main.home

import androidx.lifecycle.viewModelScope
import com.example.paceapp.core.base.BaseViewModel
import com.example.paceapp.core.domain.usecases.ObserveUserUiModelUseCase
import com.example.paceapp.features.main.home.HomeContract.Effect
import com.example.paceapp.features.main.home.HomeContract.Event
import com.example.paceapp.features.main.home.HomeContract.State
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val observeUserUiModelUseCase: ObserveUserUiModelUseCase
) : BaseViewModel<State, Event, Effect>() {

    override fun setInitialState() = State()

    override fun handleEvents(event: Event) {
        when (event) {
            is Event.Init -> initData()
            is Event.OnBackClick -> {
                setEffect { Effect.NavigateBack }
            }

            is Event.OnNotificationClick -> handleOnNotificationClick()
        }
    }


    private fun initData() {
        if (currentState.isInitialized) return

        observeUserData()
        setState { copy(isInitialized = true) }
    }

    private fun observeUserData() {
        observeUserUiModelUseCase()
            .onEach {
                setState { copy(userUiModel = it) }
            }.launchIn(viewModelScope)
    }

    private fun handleOnNotificationClick() {
    }

}
