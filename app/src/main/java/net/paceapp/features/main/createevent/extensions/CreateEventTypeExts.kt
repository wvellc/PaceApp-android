package net.paceapp.features.main.createevent.extensions

import androidx.annotation.StringRes
import net.paceapp.R
import net.paceapp.features.main.createevent.enums.EventType


val EventType.labelRes: Int
    @StringRes
    get() = when (this) {
        EventType.Run -> R.string.run
        EventType.Walk -> R.string.walk
        EventType.Cycle -> R.string.cycle
        EventType.Other -> R.string.other
    }