package com.example.paceapp.features.main.home.components

import androidx.compose.animation.Animatable
import androidx.compose.animation.core.EaseInOut
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import kotlinx.coroutines.delay
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.minutes

@Composable
fun rememberPulsingColor(
    initialColor: Color,
    targetColor: Color,
    holdDuration: Duration = 4.minutes,
    transitionDuration: Duration = 300.milliseconds
): State<Color> {
    // 1. Initialize the Animatable with the starting color
    val animatedColor = remember(initialColor) { Animatable(initialColor) }

    // 2. Pass the parameters as keys to LaunchedEffect. 
    // If any of these change, the animation safely restarts!
    LaunchedEffect(initialColor, targetColor, holdDuration, transitionDuration) {
        val holdTimeMs = holdDuration.inWholeMilliseconds
        val transitionMs = transitionDuration.inWholeMilliseconds.toInt()

        while (true) {
            delay(holdTimeMs)

            animatedColor.animateTo(
                targetValue = targetColor,
                animationSpec = tween(
                    durationMillis = transitionMs,
                    easing = EaseInOut
                )
            )

            delay(holdTimeMs)

            animatedColor.animateTo(
                targetValue = initialColor,
                animationSpec = tween(
                    durationMillis = transitionMs,
                    easing = EaseInOut
                )
            )
        }
    }

    // 3. Return it as a read-only State so your UI observes it automatically
    return animatedColor.asState()
}