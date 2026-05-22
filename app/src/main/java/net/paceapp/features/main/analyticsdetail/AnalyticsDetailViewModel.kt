package net.paceapp.features.main.analyticsdetail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import net.paceapp.core.base.BaseViewModel
import net.paceapp.core.enums.AnalyticsMetricType
import net.paceapp.core.enums.AnalyticsPeriod
import net.paceapp.core.models.AnalyticsDummyData
import net.paceapp.core.models.AnalyticsSummaryData
import net.paceapp.features.main.analyticsdetail.AnalyticsDetailContract.Effect
import net.paceapp.features.main.analyticsdetail.AnalyticsDetailContract.Event
import net.paceapp.features.main.analyticsdetail.AnalyticsDetailContract.State
import net.paceapp.features.main.analyticsdetail.navigation.AnalyticsDetailRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AnalyticsDetailViewModel @Inject constructor(
    val savedStateHandle: SavedStateHandle,
) : BaseViewModel<State, Event, Effect>() {

    override fun setInitialState() = State()

    override fun handleEvents(event: Event) {
        when (event) {
            is Event.Init -> initData()
            is Event.OnBackClick -> {
                setEffect { Effect.NavigateBack }
            }

            is Event.OnAnalyticPeriodSelected -> handleAnalyticPeriodSelected(event.period)
        }
    }


    private fun initData() {
        if (currentState.isInitialized) return
        val args = savedStateHandle.toRoute<AnalyticsDetailRoute>()
        viewModelScope.launch {
            val summaries = fetchSummaries(
                metricType = args.type,
                period = currentState.selectedPeriod
            )
            //Set argument data
            setState {
                copy(metricType = args.type, summaryList = summaries)
            }
        }

        setState { copy(isInitialized = true) }
    }

    private suspend fun fetchSummaries(
        metricType: AnalyticsMetricType,
        period: AnalyticsPeriod,
    ): List<AnalyticsSummaryData> {
        //TODO: Replace with API or Firebase response
        return AnalyticsDummyData.getSummaryList(
            metricType = metricType,
            period = period
        )
    }

    private fun handleAnalyticPeriodSelected(period: AnalyticsPeriod) {
        viewModelScope.launch {
            val summaryList = fetchSummaries(period = period, metricType = currentState.metricType)
            setState {
                copy(selectedPeriod = period, summaryList = summaryList)
            }
        }
    }
}
