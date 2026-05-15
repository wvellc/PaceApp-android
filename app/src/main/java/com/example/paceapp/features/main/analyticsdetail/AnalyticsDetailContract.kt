package com.example.paceapp.features.main.analyticsdetail

import com.example.paceapp.core.enums.AnalyticsMetricType
import com.example.paceapp.core.enums.AnalyticsPeriod
import com.example.paceapp.core.models.AnalyticsSummaryData
import com.wvelabs.core_ui.base.ViewEvent
import com.wvelabs.core_ui.base.ViewSideEffect
import com.wvelabs.core_ui.base.ViewState

class AnalyticsDetailContract {
    data class State(
        val isInitialized: Boolean = false,
        val isLoading: Boolean = false,
        val metricType: AnalyticsMetricType = AnalyticsMetricType.PACE,
        val selectedPeriod: AnalyticsPeriod = AnalyticsPeriod.DAY,
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
