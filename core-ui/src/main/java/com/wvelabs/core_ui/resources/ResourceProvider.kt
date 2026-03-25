package com.wvelabs.core_ui.resources

import androidx.compose.ui.graphics.Color

interface ResourceProvider {
    fun getString(resId: Int): String
    fun getString(resId: Int, vararg args: Any): String
    fun getColor(resId: Int): Color
}