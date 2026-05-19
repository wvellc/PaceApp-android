package com.example.paceapp.features.main.managewatch

import android.content.Context
import com.example.paceapp.core.garmin.models.WatchModel
import com.example.paceapp.features.main.managewatch.models.ManageWatchStep
import com.wvelabs.core_ui.alerts.MessageType
import com.wvelabs.core_ui.base.ViewEvent
import com.wvelabs.core_ui.base.ViewSideEffect
import com.wvelabs.core_ui.base.ViewState

class ManageWatchContract {
    data class State(
        val isInitialized: Boolean = false,
        val isLoading: Boolean = false,
        val currentStep: ManageWatchStep = ManageWatchStep.PairWatchInit,
        val watchList: List<WatchModel> = emptyList(),
        val selectedWatch: WatchModel? = null,
        val isMovingForward: Boolean = true,
        val isNextButtonEnabled: Boolean = false,
    ) : ViewState

    sealed class Event : ViewEvent {


        data object Init : Event()
        data object OnBackClick : Event()
        data class OnWatchSelected(val watch: WatchModel) : Event()
        data class OnNextButtonClick(val context: Context) : Event()
    }

    sealed class Effect : ViewSideEffect {
        data object NavigateBack : Effect()
        data class ShowToast(val message: String, val type: MessageType) : Effect()

    }
}
