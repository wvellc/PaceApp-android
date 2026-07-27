package net.paceapp.features.main.analytics

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import net.paceapp.core.enums.AnalyticsPeriod
import javax.inject.Inject
import javax.inject.Singleton

// Shared selected analytics period, so the list and the detail screen agree on it —
// the Android equivalent of iOS's single AnalyticsViewModel.selectedPeriod (default
// .week). The detail updates it on a tab change; the list observes it and re-aggregates,
// so pressing back shows data for the period selected in the detail.
@Singleton
class AnalyticsSelectionState @Inject constructor() {
    private val _selectedPeriod = MutableStateFlow(AnalyticsPeriod.WEEK)
    val selectedPeriod: StateFlow<AnalyticsPeriod> = _selectedPeriod.asStateFlow()

    fun setPeriod(period: AnalyticsPeriod) {
        _selectedPeriod.value = period
    }
}
