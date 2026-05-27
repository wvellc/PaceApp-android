package net.paceapp.features.main.createevent.helpers

import com.wvelabs.core_ui.resources.ResourceProvider
import net.paceapp.R
import net.paceapp.core.components.Validator
import net.paceapp.core.components.ValidatorType
import net.paceapp.features.main.createevent.CreateEventContract.State
import net.paceapp.features.main.createevent.enums.CreateRunStep
import javax.inject.Inject

class CreateRunValidator @Inject constructor(
    private val resourceProvider: ResourceProvider
) {
    operator fun invoke(step: CreateRunStep, state: State): String? {
        return when (step) {
            CreateRunStep.EventDetails -> {
                val eventName = state.eventNameState.text.trim().toString()
                val location = state.locationState.text.trim().toString()
                val isEventInvalid =
                    eventName.isEmpty() || Validator.validate(eventName, ValidatorType.Name) != null
                val isLocationInvalid =
                    location.isEmpty() || Validator.validate(location, ValidatorType.Name) != null

                if (isEventInvalid) {
                    return resourceProvider.getString(R.string.event_name_error_message)
                }
                if (isLocationInvalid) {
                    return resourceProvider.getString(R.string.city_error_message)
                }
                null
            }

            CreateRunStep.Distance -> {
                if (state.selectedDistance.value < 1f) {
                    return resourceProvider.getString(R.string.distance_error_message)

                }
                null
            }

            CreateRunStep.GoalTime -> {
                if (state.goalTimeInSeconds < 1) {
                    return resourceProvider.getString(R.string.goal_time_error_message)

                }
                null
            }

            CreateRunStep.SegmentDetails -> {
                null
            }

            else -> null
        }
    }
}