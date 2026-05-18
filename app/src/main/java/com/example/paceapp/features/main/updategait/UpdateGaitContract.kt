package com.example.paceapp.features.main.updategait

import com.example.paceapp.core.domain.models.GaitPace
import com.wvelabs.core_ui.base.ViewEvent
import com.wvelabs.core_ui.base.ViewSideEffect
import com.wvelabs.core_ui.base.ViewState

class UpdateGaitContract {
    data class State(
        val isInitialized: Boolean = false,
        val isLoading: Boolean = false,

        // Set Gait
        val walkingGait: GaitPace = GaitPace(1.0f),
        val runningGait: GaitPace = GaitPace(1.0f),
    ) : ViewState

    sealed class Event : ViewEvent {
        data object Init : Event()
        data object OnBackClick : Event()
        data class OnWalkingGaitChanged(val gaitPace: GaitPace) : Event()
        data class OnRunningGaitChanged(val gaitPace: GaitPace) : Event()
    }

    sealed class Effect : ViewSideEffect {
        data object NavigateBack : Effect()
    }
}
