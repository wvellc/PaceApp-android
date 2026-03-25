package com.wvelabs.core_ui.navigation

sealed interface NavEffect {
    // FIXED: Now correctly accepts NavStrategy
    data class Navigate(val dest: Any, val strategy: NavStrategy = NavStrategy.Standard) : NavEffect
    data class PopTo(val dest: Any, val inclusive: Boolean = false) : NavEffect
    data object Back : NavEffect
    data class BottomSheet(val dest: Any) : NavEffect
}