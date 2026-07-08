package net.paceapp.core.data.firestore

import com.google.firebase.Timestamp
import com.google.firebase.firestore.IgnoreExtraProperties

// Firestore user profile document (`users/{uid}`). Field names match iOS UserModel
// exactly (shared backend). All `var` + defaults for the Firestore POJO mapper.
// iOS writes nil profile strings as "" — we do the same on create for shape parity.
@IgnoreExtraProperties
data class UserDocument(
    var uuid: String = "",
    var firstName: String = "",
    var lastName: String = "",
    var gender: String = "",
    var email: String = "",
    var phoneNumber: String = "",
    var gait: GaitDocument? = null,
    var intervalVibrate: Boolean? = null,
    var intervalBeep: Boolean? = null,
    // Stored as full name ("Miles"/"Kilometers") to match iOS.
    var distanceUnit: String? = null,
    var lastSyncedAt: Timestamp? = null,
)

// Nested `gait` map. Units are the full words "Feet"/"Meters" (NOT the watch's
// ft/m — convert only at the BLE boundary). Step lengths are Doubles.
@IgnoreExtraProperties
data class GaitDocument(
    var walkingStepLength: Double = 0.0,
    var walkingUnit: String = "Feet",
    var runningStepLength: Double = 0.0,
    var runningUnit: String = "Feet",
)
