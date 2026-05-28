package com.wvelabs.core_ui.utils

import androidx.compose.runtime.staticCompositionLocalOf
import com.kyant.backdrop.Backdrop

val LocalAppBackdrop = staticCompositionLocalOf<Backdrop> {
    error("LocalAppBackdrop not found! You must wrap your screen or NavHost in CompositionLocalProvider(LocalAppBackdrop provides ...)")
}