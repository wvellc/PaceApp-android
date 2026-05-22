package net.paceapp.features.main.analytics

import net.paceapp.core.enums.AnalyticsMetricType
import net.paceapp.core.models.AnalyticsSummaryData
import com.wvelabs.core_ui.base.ViewEvent
import com.wvelabs.core_ui.base.ViewSideEffect
import com.wvelabs.core_ui.base.ViewState

class AnalyticsContract {
    data class State(
        val isInitialized: Boolean = false,
        val isLoading: Boolean = false,
        val analyticsList: List<AnalyticsSummaryData> = emptyList()
    ) : ViewState

    sealed class Event : ViewEvent {

        data object Init : Event()
        data object OnBackClick : Event()
        data class OnAnalyticsClick(val analyticsData: AnalyticsSummaryData) : Event()

    }

    sealed class Effect : ViewSideEffect {

        data object NavigateBack : Effect()
        data class NavigateToAnalyticsDetails(val type: AnalyticsMetricType) : Effect()
    }
}
