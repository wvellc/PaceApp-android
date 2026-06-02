package net.paceapp.core.domain.usecases

import com.wvelabs.core_ui.resources.ResourceProvider
import net.paceapp.R
import net.paceapp.core.components.Validator
import net.paceapp.core.components.ValidatorType
import javax.inject.Inject

/**
 * Validates the Create Event form data.
 * Pure business logic: takes raw inputs, returns a validation error string if invalid.
 */
class ValidateEventUseCase @Inject constructor(
    private val resourceProvider: ResourceProvider
) {
    operator fun invoke(eventName: String, location: String): String? {
        val name = eventName.trim()
        val loc = location.trim()

        if (name.isEmpty() || Validator.validate(name, ValidatorType.Name) != null) {
            return resourceProvider.getString(R.string.event_name_error_message)
        }
        
        if (loc.isEmpty() || Validator.validate(loc, ValidatorType.Name) != null) {
            return resourceProvider.getString(R.string.city_error_message)
        }

        return null
    }
}