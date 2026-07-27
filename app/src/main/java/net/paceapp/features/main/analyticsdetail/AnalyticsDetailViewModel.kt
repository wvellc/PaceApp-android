package net.paceapp.features.main.analyticsdetail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import net.paceapp.core.auth.AuthManager
import net.paceapp.core.base.BaseViewModel
import net.paceapp.core.data.firestore.AnalyticsRepository
import net.paceapp.core.enums.AnalyticsMetricType
import net.paceapp.core.enums.AnalyticsPeriod
import net.paceapp.core.models.AnalyticsSummaryData
import net.paceapp.features.main.analytics.AnalyticsAggregator
import net.paceapp.features.main.analytics.AnalyticsSelectionState
import net.paceapp.features.main.analyticsdetail.AnalyticsDetailContract.Effect
import net.paceapp.features.main.analyticsdetail.AnalyticsDetailContract.Event
import net.paceapp.features.main.analyticsdetail.AnalyticsDetailContract.State
import net.paceapp.features.main.analyticsdetail.navigation.AnalyticsDetailRoute
import javax.inject.Inject

@HiltViewModel
class AnalyticsDetailViewModel @Inject constructor(
    val savedStateHandle: SavedStateHandle,
    private val repository: AnalyticsRepository,
    private val authManager: AuthManager,
    private val selectionState: AnalyticsSelectionState,
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
        // Open on the shared period (default WEEK) so the duration tab is pre-selected
        // to whatever the list is showing (mirrors iOS AnalyticsViewModel.selectedPeriod).
        val period = selectionState.selectedPeriod.value
        setState { copy(metricType = args.type, selectedPeriod = period, isInitialized = true) }
        loadSummaries(metricType = args.type, period = period)
    }

    // Re-fetch for the newly selected period; each period has its own date window. Also
    // publish it to the shared state so the list reflects it when the user goes back.
    private fun handleAnalyticPeriodSelected(period: AnalyticsPeriod) {
        selectionState.setPeriod(period)
        setState { copy(selectedPeriod = period) }
        loadSummaries(metricType = currentState.metricType, period = period)
    }

    private fun loadSummaries(metricType: AnalyticsMetricType, period: AnalyticsPeriod) {
        val userId = authManager.currentUid ?: return
        setState { copy(isLoading = true) }
        viewModelScope.launch {
            val summaries = runCatching {
                val (from, to) = AnalyticsAggregator.dateRange(period)
                val records = repository.fetchCompletedEvents(userId, from, to)
                AnalyticsAggregator.summaryList(metricType, period, records)
            }.getOrElse { emptyList() }
            setState { copy(summaryList = summaries, isLoading = false) }
        }
    }
}
