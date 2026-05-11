package com.example.paceapp.features.main.history

import androidx.compose.foundation.text.input.TextFieldState
import com.example.paceapp.features.main.history.domain.HistoryUiModel
import com.wvelabs.core_ui.base.ViewEvent
import com.wvelabs.core_ui.base.ViewSideEffect
import com.wvelabs.core_ui.base.ViewState

class HistoryContract {

    data class State(
        val isInitialized: Boolean = false,
        val historyList: List<HistoryUiModel> = emptyList(),
        val searchTextState: TextFieldState = TextFieldState(),
        val hasFilterApplied: Boolean = false,
        val isLoading: Boolean = false
    ) : ViewState

    sealed class Event : ViewEvent {
        data object Init : Event()
        data object OnBackClick : Event()
        data object OnFilterClick : Event()
    }

    sealed class Effect : ViewSideEffect {
        data object NavigateBack : Effect()
    }
}
