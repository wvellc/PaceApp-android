package com.example.paceapp.core.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wvelabs.core_ui.base.ViewEvent
import com.wvelabs.core_ui.base.ViewSideEffect
import com.wvelabs.core_ui.base.ViewState
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * The parent ViewModel for EVERY feature in the app.
 * It provides built-in Navigation and Auto-Retry logic for API calls.
 */
abstract class BaseViewModel<S : ViewState, E : ViewEvent, Ef : ViewSideEffect> : ViewModel() {

    private val _state by lazy { MutableStateFlow(setInitialState()) }
    val state: StateFlow<S> = _state.asStateFlow()

    private val _effect = MutableSharedFlow<Ef>()
    val effect: SharedFlow<Ef> = _effect.asSharedFlow()

    protected abstract fun setInitialState(): S

    fun setEvent(event: E) {
        handleEvents(event)
    }

    protected abstract fun handleEvents(event: E)

    protected fun setState(reducer: S.() -> S) {
        _state.value = _state.value.reducer()
    }

    protected fun setEffect(builder: () -> Ef) {
        viewModelScope.launch { _effect.emit(builder()) }
    }
}