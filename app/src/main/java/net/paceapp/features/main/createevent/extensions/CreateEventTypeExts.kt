package net.paceapp.features.main.createevent.extensions

import androidx.annotation.StringRes
import net.paceapp.R
import net.paceapp.features.main.createevent.enums.CreateEventType


val CreateEventType.labelRes: Int?
    @StringRes
    get() = when (this) {
        CreateEventType.Run -> R.string.run
        CreateEventType.Walk -> R.string.walk
        CreateEventType.Cycling -> R.string.cycling
    }