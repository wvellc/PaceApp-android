package net.paceapp.features.main.history

import androidx.compose.foundation.text.input.TextFieldState
import net.paceapp.features.main.history.models.HistoryFilterModel
import net.paceapp.features.main.history.models.HistoryUiModel
import com.wvelabs.core_ui.base.ViewEvent
import com.wvelabs.core_ui.base.ViewSideEffect
import com.wvelabs.core_ui.base.ViewState

class HistoryContract {

    data class State(
        val isInitialized: Boolean = false,
        val historyList: List<HistoryUiModel> = emptyList(),
        val searchTextState: TextFieldState = TextFieldState(),
        val activeFilter: HistoryFilterModel? = null,
        val isLoading: Boolean = true
    ) : ViewState

    sealed class Event : ViewEvent {
        data object Init : Event()
        data object OnBackClick : Event()
        data class OnFilterChange(val filter: HistoryFilterModel?) : Event()
        data class OnHistoryClick(val history: HistoryUiModel) : Event()
        data class OnDuplicateHistoryClick(val history: HistoryUiModel) : Event()
        data class OnDeleteHistoryClick(val history: HistoryUiModel) : Event()
    }

    sealed class Effect : ViewSideEffect {
        data object NavigateBack : Effect()
    }
}
