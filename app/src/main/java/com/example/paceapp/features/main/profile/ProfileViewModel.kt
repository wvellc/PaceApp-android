package com.example.paceapp.features.main.profile

import androidx.lifecycle.viewModelScope
import com.example.paceapp.core.base.BaseViewModel
import com.example.paceapp.core.domain.usecases.ObserveUserUiModelUseCase
import com.example.paceapp.features.main.profile.ProfileContract.Effect
import com.example.paceapp.features.main.profile.ProfileContract.Event
import com.example.paceapp.features.main.profile.ProfileContract.State
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    val observeUserUiModelUseCase: ObserveUserUiModelUseCase
) : BaseViewModel<State, Event, Effect>() {
    override fun setInitialState() = State()

    override fun handleEvents(event: Event) {
        when (event) {
            is Event.Init -> initData()
            is Event.OnBackClick -> {
                setEffect { Effect.NavigateBack }
            }

            is Event.OnSettingClick -> handleOnSettingClick()
            is Event.OnEditProfileClick -> handleOnEditProfileClick()
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


    private fun handleOnSettingClick() {

    }

    private fun handleOnEditProfileClick() {
    }
}
