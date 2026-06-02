package net.paceapp.core.formatters

import com.wvelabs.core_ui.resources.ResourceProvider
import com.wvelabs.core_ui.utils.AppDateFormat
import com.wvelabs.core_ui.utils.DateTimeHelper
import kotlinx.datetime.LocalDateTime
import net.paceapp.core.domain.models.DistanceModel
import javax.inject.Inject
import kotlin.time.Duration

class ActivityFormatter @Inject constructor(
    private val resourceProvider: ResourceProvider
) {

    fun formatDate(date: LocalDateTime): String {
        return DateTimeHelper.formatDateTime(
            date = date,
            toFormat = AppDateFormat.DATE_SHORT_DM,
            isUtc = false
        ).orEmpty()
    }

    fun formatGoalTime(duration: Duration): String {
        return DateTimeHelper.formatDuration(duration)
    }

    fun formatDistance(distance: DistanceModel): String {
        return "%.2f %s".format(distance.value, resourceProvider.getString(distance.unit.titleRes))
    }
}