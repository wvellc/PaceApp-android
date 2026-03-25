package com.wvelabs.core_ui.base

/**
 * Common state interface for all screens.
 * Enforces a consistent UI behavior across the app.
 */
interface ViewState {
    val isLoading: Boolean
    val error: String?

    /**
     * Allows the CoreViewModel to update common fields 
     * without knowing the specific feature state class.
     */
    fun copyWithDefaults(
        isLoading: Boolean = this.isLoading,
        error: String? = this.error
    ): ViewState
}

interface ViewEvent

interface ViewSideEffect