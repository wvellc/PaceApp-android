package net.paceapp.core.mappers

import net.paceapp.core.data.firestore.UserDocument
import net.paceapp.core.domain.models.GaitPace
import net.paceapp.core.domain.models.UserData
import net.paceapp.core.enums.toDistanceUnitsOrNull
import net.paceapp.core.extensions.toGaitUnit
import net.paceapp.core.extensions.toGenderTypeOrNull

// Merges the live Firestore user doc onto the existing local-session UserData. Fields the
// doc doesn't carry (loginType, countryCode) are preserved from `current`, and a blank /
// missing doc field keeps the current value so a partial doc can't blank the session.
fun UserDocument.mergeInto(current: UserData): UserData = current.copy(
    firstName = firstName.ifBlank { null } ?: current.firstName,
    lastName = lastName.ifBlank { null } ?: current.lastName,
    gender = gender.toGenderTypeOrNull() ?: current.gender,
    email = email.ifBlank { null } ?: current.email,
    phoneNumber = phoneNumber.ifBlank { null } ?: current.phoneNumber,
    distanceUnits = distanceUnit?.toDistanceUnitsOrNull() ?: current.distanceUnits,
    walkingGait = gait
        ?.let { GaitPace(it.walkingStepLength.toFloat(), it.walkingUnit.toGaitUnit()) }
        ?: current.walkingGait,
    runningGait = gait
        ?.let { GaitPace(it.runningStepLength.toFloat(), it.runningUnit.toGaitUnit()) }
        ?: current.runningGait,
)
