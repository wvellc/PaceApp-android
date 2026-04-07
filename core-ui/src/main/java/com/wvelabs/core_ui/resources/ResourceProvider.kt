package com.wvelabs.core_ui.resources

import androidx.annotation.PluralsRes
import androidx.annotation.StringRes

interface ResourceProvider {
    fun getString(@StringRes resId: Int): String
    fun getString(@StringRes resId: Int, vararg args: Any): String

    fun getQuantityString(@PluralsRes id: Int, quantity: Int, vararg formatArgs: Any): String
}