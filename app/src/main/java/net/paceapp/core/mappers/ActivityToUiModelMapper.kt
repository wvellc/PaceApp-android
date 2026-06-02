package net.paceapp.core.mappers

import net.paceapp.core.models.ActivityModel
import net.paceapp.core.models.ActivityUiModel
import net.paceapp.core.formatters.ActivityFormatter
import javax.inject.Inject
import kotlin.time.Duration.Companion.seconds


class ActivityToUiModelMapper @Inject constructor(
    private val formatter: ActivityFormatter
) {

    fun map(
        model: ActivityModel
    ): ActivityUiModel {
        return ActivityUiModel(
            id = model.id,
            title = model.title,
            date = formatter.formatDate(model.date),
            location = model.location,
            distance = formatter.formatDistance(model.distance),
            goalTime = formatter.formatGoalTime(model.goalTime.seconds),
            avgPace = model.avgPace,
            paceDifference = model.paceDifference,
            isAheadOfTime = model.isAheadOfTime
        )
    }
}