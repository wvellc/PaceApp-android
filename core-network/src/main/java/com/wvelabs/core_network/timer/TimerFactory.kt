package com.wvelabs.core_network.timer

import kotlinx.coroutines.CoroutineScope
import javax.inject.Inject
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

/**
 * Factory to create instances of [TimerEngine].
 * * ### Usage Instructions:
 * 1. **Screen-Scoped Timer (OTP, UI Clocks):**
 * Use the [create] method passing the [viewModelScope].
 * ```
 * val timer = timerFactory.create(viewModelScope, 1.seconds)
 * ```
 * * 2. **Smooth Progress Animations:**
 * If the UI requires a fluid progress bar, provide a millisecond interval.
 * ```
 * val smoothTimer = timerFactory.create(viewModelScope, 10.milliseconds)
 * ```
 */
class TimerFactory @Inject constructor() {
    /**
     * Creates a new [TimerEngine] instance.
     * * @param scope The lifecycle-bound scope (e.g., viewModelScope or ApplicationScope).
     * @param interval The tick frequency. Defaults to [1.seconds].
     * Minimum allowed: [10.milliseconds].
     */
    fun create(scope: CoroutineScope, interval: Duration = 1.seconds) =
        TimerEngine(scope, interval)
}