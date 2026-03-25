package com.wvelabs.core_ui.navigation

enum class NavStrategy {
    Standard,       // Normal push
    SingleTop,      // Avoid multiple instances of same screen
    ClearBackstack  // Clear everything (useful for Logout/Login)
}