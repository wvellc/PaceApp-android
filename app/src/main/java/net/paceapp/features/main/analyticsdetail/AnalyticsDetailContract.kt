package net.paceapp.features.main.analyticsdetail

import net.paceapp.core.enums.AnalyticsMetricType
import net.paceapp.core.enums.AnalyticsPeriod
import net.paceapp.core.models.AnalyticsSummaryData
import com.wvelabs.core_ui.base.ViewEvent
import com.wvelabs.core_ui.base.ViewSideEffect
import com.wvelabs.core_ui.base.ViewState

class AnalyticsDetailContract {
    data class State(
        val isInitialized: Boolean = false,
        val isLoading: Boolean = false,
        val metricType: AnalyticsMetricType = AnalyticsMetricType.PACE,
        val selectedPeriod: AnalyticsPeriod = AnalyticsPeriod.WEEK,
        val summaryList: List<AnalyticsSummaryData> = emptyList()
    ) : ViewState

    sealed class Event : ViewEvent {

        data object Init : Event()
        data object OnBackClick : Event()
        data class OnAnalyticPeriodSelected(val period: AnalyticsPeriod) : Event()

    }

    sealed class Effect : ViewSideEffect {
        data object NavigateBack : Effect()
    }
}
