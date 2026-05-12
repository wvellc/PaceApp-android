package com.example.paceapp.features.main.home

import com.example.paceapp.core.domain.models.UserUiModel
import com.wvelabs.core_ui.base.ViewEvent
import com.wvelabs.core_ui.base.ViewSideEffect
import com.wvelabs.core_ui.base.ViewState

class HomeContract {
    data class State(
        val isInitialized: Boolean = false,
        val isLoading: Boolean = false,
        val userUiModel: UserUiModel? = null,
    ) : ViewState

    sealed class Event : ViewEvent {
        data object Init : Event()
        data object OnBackClick : Event()
       data object OnNotificationClick : Event()
    }

    sealed class Effect : ViewSideEffect {
        data object NavigateBack : Effect()
    }
}
