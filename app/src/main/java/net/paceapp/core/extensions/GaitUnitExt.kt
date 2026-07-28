package net.paceapp.core.extensions

import androidx.annotation.StringRes
import net.paceapp.R
import net.paceapp.core.domain.models.GaitUnit

val GaitUnit.titleRes: Int
    @StringRes
    get() = when (this) {
    GaitUnit.FEET -> R.string.feet
      GaitUnit.METERS -> R.string.meters
    }

// GaitUnit → Firestore/app full word ("Feet"/"Meters"). Convert only at this boundary.
fun GaitUnit.firestoreName(): String = if (this == GaitUnit.METERS) "Meters" else "Feet"

// GaitUnit → watch BLE short form ("m"/"ft").
fun GaitUnit.watchName(): String = if (this == GaitUnit.METERS) "m" else "ft"
