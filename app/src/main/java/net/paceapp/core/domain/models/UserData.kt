package net.paceapp.core.domain.models

import net.paceapp.core.domain.enums.GenderTypes
import net.paceapp.core.domain.enums.LoginTypes
import net.paceapp.core.enums.DistanceUnits
import net.paceapp.session.AppSessionManager
import kotlinx.serialization.Serializable

/**
 * ⚠️ Architectural Note:
 * Do not pass [UserData] directly as a navigation route argument.
 * Doing so requires building complex custom NavTypes for Compose Navigation.
 * * Instead, pass the User ID (if needed) or retrieve the data directly
 * from the [AppSessionManager] / DataStore.
 */
@Serializable
data class UserData(
    val id: String? = null,
    val firstName: String? = null,
    val lastName: String? = null,
    val loginType: LoginTypes = LoginTypes.EMAIL,
    val gender: GenderTypes = GenderTypes.MALE,
    val distanceUnits: DistanceUnits = DistanceUnits.MILES,
    val email: String? = null,
    val countryCode: String? = null,
    val phoneNumber: String? = null,
    val walkingGait: GaitPace? = null,
    val runningGait: GaitPace? = null
)


val UserData.isProfileComplete: Boolean
    get() = !this.firstName.isNullOrBlank() && !this.lastName.isNullOrBlank()