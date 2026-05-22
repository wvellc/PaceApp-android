package net.paceapp.features.main.tabhost

import net.paceapp.features.main.home.models.WatchMetric
import com.wvelabs.core_ui.base.ViewEvent
import com.wvelabs.core_ui.base.ViewSideEffect
import com.wvelabs.core_ui.base.ViewState

class TabHostContract {
    data class State(
        val isInitialized: Boolean = false,
        val isLoading: Boolean = false,
        val metrics: List<WatchMetric> = emptyList(),
    ) : ViewState

    sealed class Event : ViewEvent {
        data object Init : Event()
    }

    sealed class Effect : ViewSideEffect {
    }
}
