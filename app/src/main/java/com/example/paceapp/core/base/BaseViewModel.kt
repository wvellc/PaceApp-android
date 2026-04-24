package com.example.paceapp.core.base

import androidx.lifecycle.viewModelScope
import com.example.paceapp.BuildConfig
import com.wvelabs.core_ui.base.CoreViewModel
import com.wvelabs.core_ui.base.ViewEvent
import com.wvelabs.core_ui.base.ViewSideEffect
import com.wvelabs.core_ui.base.ViewState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch

/**
 * The parent ViewModel for EVERY feature in the app.
 * It provides built-in Navigation and Auto-Retry logic for API calls.
 */
abstract class BaseViewModel<S : ViewState, E : ViewEvent, Ef : ViewSideEffect> :
    CoreViewModel<S, E, Ef>() {
    protected val currentState: S get() = state.value
    protected val isDebugMode: Boolean = BuildConfig.DEBUG
    /**
     * A generic helper to observe any Flow and update the state automatically.
     * Uses distinctUntilChanged() to prevent infinite recomposition loops.
     */
    protected fun <T> observeState(
        flow: Flow<T>,
        updateState: S.(T) -> S
    ) {
        viewModelScope.launch {
            flow.distinctUntilChanged().collectLatest { value ->
                setState { updateState(value) }
            }
        }
    }
}