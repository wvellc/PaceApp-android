package net.paceapp.features.main.history

import androidx.compose.foundation.text.input.TextFieldState
import com.wvelabs.core_ui.base.ViewEvent
import com.wvelabs.core_ui.base.ViewSideEffect
import com.wvelabs.core_ui.base.ViewState
import net.paceapp.core.models.ActivityUiModel
import net.paceapp.features.main.history.models.HistoryFilterModel

class HistoryContract {

    data class State(
        val isInitialized: Boolean = false,
        val historyList: List<ActivityUiModel> = emptyList(),
        val searchTextState: TextFieldState = TextFieldState(),
        val activeFilter: HistoryFilterModel? = null,
        val isLoading: Boolean = true
    ) : ViewState

    sealed class Event : ViewEvent {
        data object Init : Event()
        data object OnBackClick : Event()
        data class OnFilterChange(val filter: HistoryFilterModel?) : Event()
        data class OnHistoryClick(val history: ActivityUiModel) : Event()
        data class OnDuplicateHistoryClick(val history: ActivityUiModel) : Event()
        data class OnDeleteHistoryClick(val history: ActivityUiModel) : Event()
    }

    sealed class Effect : ViewSideEffect {
        data object NavigateBack : Effect()
        data class NavigateToEventDetails(
            val id: String,
            val eventName: String,
            val location: String,
            val date: String,
        ) : Effect()

        data class NavigateToDuplicateEvent(
            val id: String,
            val eventName: String,
            val location: String,
            val date: String,
        ) : Effect()
    }
}
