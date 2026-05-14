package com.example.paceapp.features.main.home.mappers

import com.example.paceapp.features.main.home.domain.models.ActivityDomainModel
import com.example.paceapp.features.main.home.formatters.ActivityFormatter
import com.example.paceapp.features.main.home.models.ActivityUiModel
import javax.inject.Inject


class ActivityUiMapper @Inject constructor(
    private val formatter: ActivityFormatter
) {

    fun map(
        model: ActivityDomainModel
    ): ActivityUiModel {
        return ActivityUiModel(
            id = model.id,
            title = model.name,
            date = formatter.formatDate(model.date),
            location = model.location,
            distance = formatter.formatDistance(model.distanceMiles),
            goalTime = formatter.formatGoalTime(model.goalTime)
        )
    }
}