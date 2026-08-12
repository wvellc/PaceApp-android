package net.paceapp.core.extensions

import androidx.annotation.StringRes
import net.paceapp.R
import net.paceapp.core.domain.enums.GenderTypes

val GenderTypes.titleRes: Int
    @StringRes
    get() = when (this) {
        GenderTypes.MALE -> R.string.male
        GenderTypes.FEMALE -> R.string.female
        GenderTypes.OTHER -> R.string.other
    }

// GenderTypes ↔ Firestore string ("Male"/"Female"/"Other") — matches iOS Gender.rawValue.
fun GenderTypes.firestoreName(): String = when (this) {
    GenderTypes.MALE -> "Male"
    GenderTypes.FEMALE -> "Female"
    GenderTypes.OTHER -> "Other"
}

fun String.toGenderTypeOrNull(): GenderTypes? = when (trim().lowercase()) {
    "male" -> GenderTypes.MALE
    "female" -> GenderTypes.FEMALE
    "other" -> GenderTypes.OTHER
    else -> null
}
