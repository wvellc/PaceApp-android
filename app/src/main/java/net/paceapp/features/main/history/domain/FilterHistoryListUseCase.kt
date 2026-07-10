package net.paceapp.features.main.history.domain

import net.paceapp.features.main.history.models.HistoryFilterModel
import com.wvelabs.core_network.di.DefaultDispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import net.paceapp.core.models.ActivityUiModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

class FilterHistoryListUseCase @Inject constructor(
    @param:DefaultDispatcher private val defaultDispatcher: CoroutineDispatcher
) {
    suspend operator fun invoke(
        activities: List<ActivityUiModel>,
        filter: HistoryFilterModel?,
        searchQuery: String?,
    ): List<ActivityUiModel> = withContext(defaultDispatcher) {

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

            // Distance Filter — parse the leading numeric token from the display
            // string ("5.00 Miles" / "5.00 Kms"), ignoring the unit word. The old
            // `.replace(" mi", "")` never matched the "Miles"/"Kms" suffix, so every
            // row parsed to 0f and dropped out once the min slider left 0.
            val matchesDistance = filter?.let {
                // Parse the leading numeric token from "5.00 Miles"/"5,00 Kms",
                // normalising a comma decimal separator so comma-decimal locales
                // (where "%.2f" renders "5,00") still parse instead of dropping to 0f.
                val activityDistance = activity.distance
                    .substringBefore(' ')
                    .replace(',', '.')
                    .toFloatOrNull() ?: 0f
                activityDistance in it.distanceRange
            } ?: true

            // Date Filter — the display date (DATE_SHORT_DM) uses hardcoded English
            // month names, so format the filter date with the SAME (English) locale;
            // Locale.getDefault() diverged on non-English devices and matched nothing.
            val matchesDate = filter?.dateMillis?.let { filterMillis ->
                val filterDateStr = SimpleDateFormat("dd MMM", Locale.ENGLISH)
                    .format(Date(filterMillis))
                activity.date.equals(filterDateStr, ignoreCase = true)
            } ?: true

            // Location Filter — match the event's location, not its title (name).
            val matchesLocation = filter?.location?.takeIf { it.isNotBlank() }?.let { loc ->
                activity.location.contains(loc, ignoreCase = true)
            } ?: true

            // The item must match ALL active criteria to stay in the list
            matchesSearch && matchesDistance && matchesDate && matchesLocation
        }
    }
}