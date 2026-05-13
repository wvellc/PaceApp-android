package com.example.paceapp.core.domain.usecases

import com.example.paceapp.core.domain.enums.LoginTypes
import com.example.paceapp.core.domain.models.UserUiModel
import com.example.paceapp.core.extensions.titleRes
import com.example.paceapp.core.garmin.enums.WatchConnectionState
import com.example.paceapp.session.AppSessionManager
import com.wvelabs.core_ui.resources.ResourceProvider
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ObserveUserUiModelUseCase @Inject constructor(
    private val sessionManager: AppSessionManager,
    private val resourceProvider: ResourceProvider,
) {
    operator fun invoke(): Flow<UserUiModel?> {
        return sessionManager.observeUserDetails().map { sessionUser ->
            if (sessionUser == null) {
                return@map null
            }

            val fName = sessionUser.firstName.orEmpty()
            val lName = sessionUser.lastName.orEmpty()

            // Get Full Name
            val fullName = listOf(fName, lName)
                .filter { it.isNotBlank() }
                .joinToString(" ")
                .ifBlank { "Unknown User" }
            val gender = resourceProvider.getString(sessionUser.gender.titleRes)
            val fullNameAndGender = "$fullName, $gender"
            // Get Email vs Phone based on LoginType
            val contactInfo = when (sessionUser.loginType) {
                LoginTypes.PHONE -> {
                    val code = sessionUser.countryCode.orEmpty()
                    val num = sessionUser.phoneNumber.orEmpty()
                    if (num.isBlank()) sessionUser.email.orEmpty() else "$code $num".trim()
                }

                else -> sessionUser.email.orEmpty()
            }.ifBlank { "No contact info" }



            UserUiModel(
                id = sessionUser.id.orEmpty(),
                firstName = fName,
                lastName = lName,
                fullName = fullName,
                fullNameAndGender = fullNameAndGender,
                contactInfo = contactInfo,
                loginType = sessionUser.loginType,
            )
        }
    }
}