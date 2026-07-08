package net.paceapp.features.main.analytics

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import net.paceapp.core.auth.AuthManager
import net.paceapp.core.base.BaseViewModel
import net.paceapp.core.data.firestore.AnalyticsRepository
import net.paceapp.core.enums.AnalyticsPeriod
import net.paceapp.core.models.AnalyticsSummaryData
import net.paceapp.features.main.analytics.AnalyticsContract.Effect
import net.paceapp.features.main.analytics.AnalyticsContract.Event
import net.paceapp.features.main.analytics.AnalyticsContract.State
import javax.inject.Inject

@HiltViewModel
class AnalyticsViewModel @Inject constructor(
    private val repository: AnalyticsRepository,
    private val authManager: AuthManager,
) : BaseViewModel<State, Event, Effect>() {

    override fun setInitialState() = State()

    override fun handleEvents(event: Event) {
        when (event) {
            is Event.Init -> initData()
            is Event.OnBackClick -> {
                setEffect { Effect.NavigateBack }
            }

            is Event.OnAnalyticsClick -> handleAnalyticsClick(event.analyticsData)
        }
    }


    private fun initData() {
        if (currentState.isInitialized) return
        setState { copy(isInitialized = true) }
        fetchAnalyticsData()
    }

    // Main list shows one card per metric aggregated over the last week (matches iOS).
    private fun fetchAnalyticsData() {
        val userId = authManager.currentUid ?: return
        val period = AnalyticsPeriod.WEEK
        setState { copy(isLoading = true) }
        viewModelScope.launch {
            val (from, to) = AnalyticsAggregator.dateRange(period)
            val list = runCatching {
                val records = repository.fetchCompletedEvents(userId, from, to)
                AnalyticsAggregator.analyticsList(period, records)
            }.getOrElse { emptyList() }
            setState { copy(analyticsList = list, isLoading = false) }
        }
    }

    private fun handleAnalyticsClick(analyticsData: AnalyticsSummaryData) {
        setEffect { Effect.NavigateToAnalyticsDetails(analyticsData.type) }
    }

}
