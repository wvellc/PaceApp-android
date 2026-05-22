package net.paceapp.features.main.analytics

import net.paceapp.core.base.BaseViewModel
import net.paceapp.features.main.analytics.AnalyticsContract.Effect
import net.paceapp.features.main.analytics.AnalyticsContract.Event
import net.paceapp.features.main.analytics.AnalyticsContract.State
import net.paceapp.core.models.AnalyticsDummyData
import net.paceapp.core.models.AnalyticsSummaryData
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AnalyticsViewModel @Inject constructor() : BaseViewModel<State, Event, Effect>() {

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
        fetchAnalyticsData()
        setState { copy(isInitialized = true) }
    }

    private fun fetchAnalyticsData() {
        setState { copy(analyticsList = AnalyticsDummyData.getAnalyticsList()) }
    }

    private fun handleAnalyticsClick(analyticsData: AnalyticsSummaryData) {
        setEffect { Effect.NavigateToAnalyticsDetails(analyticsData.type) }
    }

}
