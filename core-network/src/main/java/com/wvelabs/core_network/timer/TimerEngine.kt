package com.wvelabs.core_network.timer

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

/**
 * A Wall-Clock based Timer Engine.
 * * @param scope The CoroutineScope where the timer job runs (e.g., viewModelScope).
 * @param tickInterval The frequency of time updates.
 * - Use [1.seconds] for standard text-based clocks (Battery Efficient).
 * - Use [10.milliseconds] to [30.milliseconds] for smooth, "liquid" progress bar animations.
 */
class TimerEngine(
    private val scope: CoroutineScope,
    private val tickInterval: Duration = 1.seconds
) {
    init {
        // Guardrail: 10ms is the "Gold Standard" floor for mobile UI performance
        require(tickInterval >= 10.milliseconds) {
            "Timer tickInterval cannot be less than 10ms to prevent performance degradation."
        }
    }

    private var timerJob: Job? = null

    // The "Anchor" - everything is calculated relative to this
    private var targetTimestamp: Long = 0L
    private var initialDuration: Duration = Duration.ZERO
    private var isCountDownMode: Boolean = false
    private var pausedRemainingTime: Duration = Duration.ZERO

    private val _time = MutableStateFlow(Duration.ZERO)
    val time: StateFlow<Duration> = _time.asStateFlow()

    private val _isRunning = MutableStateFlow(false)
    val isRunning: StateFlow<Boolean> = _isRunning.asStateFlow()

    private val _timerFinishedEvent = MutableSharedFlow<Unit>()
    val timerFinishedEvent = _timerFinishedEvent.asSharedFlow()

    /**
     * Progress calculated dynamically.
     * If tickInterval is small (e.g. 10ms-50ms), this will be liquid smooth.
     */
    val progress: Flow<Float> = _time.map { current ->
        if (initialDuration == Duration.ZERO) 0f
        else (current.inWholeMilliseconds.toDouble() / initialDuration.inWholeMilliseconds.toDouble())
            .coerceIn(0.0, 1.0).toFloat()
    }

    /**
     * @param duration Total time for the session.
     * @param isCountdown If true, counts down from duration to 0. If false, counts up from 0 to duration.
     */
    fun start(duration: Duration? = null, isCountdown: Boolean = false) {
        if (_isRunning.value) return

        duration?.let { initialDuration = it }
        isCountDownMode = isCountdown

        // Set the Wall Clock anchor
        if (pausedRemainingTime > Duration.ZERO) {
            // Resuming from a pause
            targetTimestamp = System.currentTimeMillis() + pausedRemainingTime.inWholeMilliseconds
            pausedRemainingTime = Duration.ZERO
        } else {
            // Fresh start
            targetTimestamp = System.currentTimeMillis() + initialDuration.inWholeMilliseconds
        }

        runTimerLoop()
    }

    private fun runTimerLoop() {
        _isRunning.value = true
        timerJob = scope.launch {
            while (isActive) {
                val now = System.currentTimeMillis()
                val remaining = (targetTimestamp - now).milliseconds

                if (isCountDownMode) {
                    _time.value = remaining.coerceAtLeast(Duration.ZERO)
                    if (_time.value <= Duration.ZERO) {
                        handleTimerFinished()
                        break
                    }
                } else {
                    // Count up logic: (Total - Remaining)
                    val elapsed = initialDuration - remaining
                    _time.value = elapsed.coerceAtLeast(Duration.ZERO)
                    if (elapsed >= initialDuration) {
                        handleTimerFinished()
                        break
                    }
                }
                delay(tickInterval)
            }
        }
    }

    fun pause() {
        if (!_isRunning.value) return
        // Capture exactly how much time was left at the moment of pause
        pausedRemainingTime = (targetTimestamp - System.currentTimeMillis()).milliseconds
        timerJob?.cancel()
        _isRunning.value = false
    }

    fun resume() = start()

    fun restart() {
        stop()
        start(initialDuration, isCountDownMode)
    }

    fun stop() {
        timerJob?.cancel()
        _isRunning.value = false
        pausedRemainingTime = Duration.ZERO
        _time.value = if (isCountDownMode) initialDuration else Duration.ZERO
    }

    private suspend fun handleTimerFinished() {
        _isRunning.value = false
        _time.value = if (isCountDownMode) Duration.ZERO else initialDuration
        timerJob?.cancel()
        _timerFinishedEvent.emit(Unit)
    }
}