package com.example.paceapp.core.domain.models

import com.example.paceapp.core.domain.enums.LoginTypes
import kotlinx.serialization.Serializable
import com.example.paceapp.session.AppSessionManager
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
    val email: String? = null,
    val countryCode: String? = null,
    val phoneNumber: String? = null,
    val profilePic: String? = null
)

val UserData.isProfileComplete: Boolean
    get() = !this.firstName.isNullOrBlank() && !this.lastName.isNullOrBlank()