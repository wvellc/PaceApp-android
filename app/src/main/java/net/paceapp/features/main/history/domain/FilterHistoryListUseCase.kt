package net.paceapp.features.main.history.domain

import net.paceapp.features.main.history.models.HistoryFilterModel
import net.paceapp.features.main.history.models.HistoryUiModel
import com.wvelabs.core_network.di.DefaultDispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

class FilterHistoryListUseCase @Inject constructor(
    @param:DefaultDispatcher private val defaultDispatcher: CoroutineDispatcher
) {
    suspend operator fun invoke(
        activities: List<HistoryUiModel>,
        filter: HistoryFilterModel?,
        searchQuery: String?,
    ): List<HistoryUiModel> = withContext(defaultDispatcher) {

        // If filter is null and search both are null/empty return the original list
        if (filter == null && searchQuery.isNullOrBlank()) {
            return@withContext activities
        }

        // Perform the heavy filtering on the Default dispatcher
        activities.filter { activity ->

            // Search Query Logic
            val matchesSearch = if (!searchQuery.isNullOrBlank()) {
                activity.title.contains(searchQuery, ignoreCase = true)
            } else {
                true // Search is empty, so it passes
            }

            // Distance Filter
            val matchesDistance = filter?.let {
                val activityDistance = activity.distance
                    .replace(" mi", "")
                    .toFloatOrNull() ?: 0f
                activityDistance in it.distanceRange
            } ?: true

            // Date Filter
            val matchesDate = filter?.dateMillis?.let { filterMillis ->
                val filterDateStr = SimpleDateFormat("dd MMM", Locale.getDefault())
                    .format(Date(filterMillis))
                activity.date.equals(filterDateStr, ignoreCase = true)
            } ?: true

            // Location Filter
            val matchesLocation = filter?.location?.takeIf { it.isNotBlank() }?.let { loc ->
                activity.title.contains(loc, ignoreCase = true)
            } ?: true

            // The item must match ALL active criteria to stay in the list
            matchesSearch && matchesDistance && matchesDate && matchesLocation
        }
    }
}