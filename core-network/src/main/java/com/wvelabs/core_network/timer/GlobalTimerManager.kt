package com.wvelabs.core_network.timer

import com.wvelabs.core_network.di.ApplicationScope
import com.wvelabs.core_network.session.TimerCache
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

@Singleton
class GlobalTimerManager @Inject constructor(
    @param:ApplicationScope private val scope: CoroutineScope,
    private val timerCache: TimerCache,
) {
    // High-precision engine fueled by the Application Scope
    private val engine = TimerEngine(scope, 1.seconds)

    val time = engine.time
    val progress = engine.progress
    val isRunning = engine.isRunning
    val finishedEvent = engine.timerFinishedEvent

    init {
        // Automatically recover state on app launch
        syncWithRealTime()
    }

    /**
     * Starts a countdown that survives app kills and backgrounding.
     */
    fun startGlobal(duration: Duration, isCountdown: Boolean = true) {
        val target = System.currentTimeMillis() + duration.inWholeMilliseconds
        scope.launch {
            timerCache.saveTimerTarget(target)
        }
        // Explicitly pass duration to engine to ensure it overrides any old state
        engine.start(duration, isCountdown = isCountdown)
    }

    /**
     * Restores the timer state by comparing the saved target to current time.
     */
    fun syncWithRealTime() {
        scope.launch {
            val savedTarget = timerCache.getTimerTarget()
            if (savedTarget > 0) {
                val now = System.currentTimeMillis()
                val remaining = (savedTarget - now).milliseconds

                if (remaining > Duration.ZERO) {
                    // Start the engine with the remaining duration
                    engine.start(remaining, isCountdown = true)
                } else {
                    // Clean up if time passed while app was closed
                    stop()
                }
            }
        }
    }

    fun restart() {
        engine.restart()
        // Recalculate and save the new target
        val newTarget = System.currentTimeMillis() + engine.time.value.inWholeMilliseconds
        scope.launch { timerCache.saveTimerTarget(newTarget) }
    }

    fun pause() {
        engine.pause()
        // When pausing a global timer, we clear the target so it doesn't
        // "keep running" in the background while the user thinks it's paused.
        scope.launch { timerCache.clearTimerTarget() }
    }

    fun resume() {
        if (engine.isRunning.value) return
        val remaining = engine.time.value
        if (remaining > Duration.ZERO) {
            val newTarget = System.currentTimeMillis() + remaining.inWholeMilliseconds
            scope.launch { timerCache.saveTimerTarget(newTarget) }
            engine.resume()
        }
    }

    fun stop() {
        engine.stop()
        scope.launch { timerCache.clearTimerTarget() }
    }
}