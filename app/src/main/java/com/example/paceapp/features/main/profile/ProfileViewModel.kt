package com.example.paceapp.features.main.profile

import androidx.lifecycle.viewModelScope
import com.example.paceapp.core.base.BaseViewModel
import com.example.paceapp.core.domain.usecases.ObserveUserUiModelUseCase
import com.example.paceapp.features.main.profile.ProfileContract.Effect
import com.example.paceapp.features.main.profile.ProfileContract.Event
import com.example.paceapp.features.main.profile.ProfileContract.State
import com.example.paceapp.features.main.profile.enums.ProfileOptions
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
            is Event.OnToggleSwitch -> handleOnToggleSwitch(event.option, event.isEnabled)
            is Event.OnProfileOptionClick -> handleOnProfileOptionClick(event.option)
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
        setEffect { Effect.NavigateToSettings }
    }

    private fun handleOnEditProfileClick() {
        setEffect { Effect.NavigateToEditProfile }
    }

    private fun handleOnProfileOptionClick(option: ProfileOptions) {
        when (option) {
            ProfileOptions.MANAGE_YOUR_WATCH -> setEffect { Effect.NavigateToManageWatch }
            ProfileOptions.INTERVAL_VIBRATE -> setState {
                copy(isIntervalVibrateEnabled = !isIntervalVibrateEnabled)
            }

            ProfileOptions.INTERVAL_BEEP -> setState {
                copy(isIntervalBeepEnabled = !isIntervalBeepEnabled)
            }

            ProfileOptions.SET_GAIT -> setEffect { Effect.NavigateToSetGait }
        }
    }

    private fun handleOnToggleSwitch(option: ProfileOptions, enabled: Boolean) {
        if (option == ProfileOptions.INTERVAL_VIBRATE) {
            setState {
                copy(isIntervalVibrateEnabled = enabled)
            }
        } else if (option == ProfileOptions.INTERVAL_BEEP) {
            setState {
                copy(isIntervalBeepEnabled = enabled)
            }
        }
    }


}
