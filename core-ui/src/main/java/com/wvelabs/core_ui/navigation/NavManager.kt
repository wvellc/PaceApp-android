package com.wvelabs.core_ui.navigation

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
/**
 * The Central Navigation Hub.
 * Injected as a Singleton via Hilt.
 */
class NavManager(
    private val scope: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)
) {
    // 1. Effects: From ViewModel -> UI (NavController)
    private val _effects = Channel<NavEffect>(Channel.BUFFERED)
    val effects = _effects.receiveAsFlow()

    // 2. Results: From Screen B -> Screen A (State-less communication)
    private val _results = MutableSharedFlow<NavResult>(replay = 0)
    val results = _results.asSharedFlow()

    fun navigate(destination: Any, strategy: NavStrategy = NavStrategy.Standard) {
        scope.launch { _effects.send(NavEffect.Navigate(destination, strategy)) }
    }

    fun goBack() {
        scope.launch { _effects.send(NavEffect.Back) }
    }

    fun popTo(destination: Any, inclusive: Boolean = false) {
        scope.launch { _effects.send(NavEffect.PopTo(destination, inclusive)) }
    }

    fun sendResult(data: Any, source: String? = null) {
        scope.launch { _results.emit(NavResult(data, source)) }
    }
}