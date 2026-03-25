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
    abstract fun setInitialState(): S

    /**
     * Handle user events.
     */
    abstract fun handleEvents(event: E)

    private val initialState: S by lazy { setInitialState() }

    private val _viewState = MutableStateFlow(initialState)
    val viewState: StateFlow<S> = _viewState.asStateFlow()

    private val _effect = Channel<Ef>(Channel.BUFFERED)
    val effect = _effect.receiveAsFlow()

    protected val currentState: S get() = _viewState.value

    /**
     * Public entry point for events.
     */
    fun onEvent(event: E) {
        handleEvents(event)
    }

    protected fun updateState(reducer: S.() -> S) {
        _viewState.update { it.reducer() }
    }

    protected fun sendEffect(effect: Ef) {
        viewModelScope.launch { _effect.send(effect) }
    }

    /**
     * Updated version of your executeCatching logic.
     * To be used with the core-network module.
     */
    fun <T> safeLaunch(
        block: suspend () -> T,
        onSuccess: (T) -> Unit,
        onError: ((Throwable) -> Unit)? = null,
        showLoading: Boolean = true,
    ) {
        viewModelScope.launch {
            if (showLoading) updateLoading(true)
            try {
                val result = block()
                onSuccess(result)
            } catch (e: Exception) {
                onError?.invoke(e)
            } finally {
                if (showLoading) updateLoading(false)
            }
        }
    }

    @Suppress("UNCHECKED_CAST")
    fun updateLoading(loading: Boolean) {
        updateState {
            copyWithDefaults(isLoading = loading) as S
        }
    }
}
