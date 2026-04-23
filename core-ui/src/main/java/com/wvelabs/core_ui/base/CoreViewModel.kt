package com.wvelabs.core_ui.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * Base ViewModel for MVI pattern.
 */
abstract class CoreViewModel<S : ViewState, E : ViewEvent, Ef : ViewSideEffect> : ViewModel() {
    /**
     * Define the initial state of the view.
     */
    protected abstract fun setInitialState(): S

    /**
     * Handle user events.
     */
    protected abstract fun handleEvents(event: E)

    private val initialState: S by lazy { setInitialState() }

    // RENAMED: _viewState -> _state to match old BaseViewModel
    private val _state = MutableStateFlow(initialState)
    val state: StateFlow<S> = _state.asStateFlow()

    private val _effect = Channel<Ef>(Channel.BUFFERED)
    open val effect = _effect.receiveAsFlow()

    /**
     * Public entry point for events.
     */
    fun setEvent(event: E) {
        handleEvents(event)
    }

    // Reducer pattern
    protected fun setState(reducer: S.() -> S) {
        _state.update { it.reducer() }
    }

    //  Lambda builder for effects (e.g., setEffect { Effect.Navigate })
    protected fun setEffect(builder: () -> Ef) {
        viewModelScope.launch { _effect.send(builder()) }
    }

    /**
     * Updated version of your executeCatching logic.
     * To be used with the core-network module.
     */
    fun <T> safeLaunch(
        block: suspend () -> T,
        onSuccess: (T) -> Unit,
        onError: ((Throwable) -> Unit)? = null,
        onLoading: ((Boolean) -> Unit)? = null,
    ) {
        viewModelScope.launch {
            onLoading?.invoke(true)
            try {
                val result = block()
                onSuccess(result)
            } catch (e: Exception) {
                onError?.invoke(e)
            } finally {
                onLoading?.invoke(false)
            }
        }
    }

}
