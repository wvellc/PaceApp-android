package com.example.paceapp.features.authentication.buildprofile.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

@Composable
fun RippleContainer(
    modifier: Modifier = Modifier,
    rippleCount: Int = 3,
    staggerDelayMillis: Long = 1000L,
    circleSize: Dp = 400.dp,
    circleColor: Color = Color.Blue,
    maxScaleMultiplier: Float = 1.5f,
    peakAlpha: Float = 0.1f,
    content: @Composable () -> Unit = {}
) {
    // Calculate absolute total lifespan duration based on your strict multiplier math
    val totalDuration = remember(rippleCount, staggerDelayMillis) {
        (rippleCount * staggerDelayMillis).toInt()
    }

    // Initialize and pre-warm animation states so they appear instantly stepped on layout frame 1
    val rippleAnimatables = remember(rippleCount, totalDuration) {
        List(rippleCount) { index ->
            val preWarmedStartProgress = (index * staggerDelayMillis).toFloat() / totalDuration
            Animatable(preWarmedStartProgress)
        }
    }

    suspend fun runContinuousLoop(animatable: Animatable<Float, *>) {
        var isInitialRun = true
        while (true) {
            val currentProgress = animatable.value
            val remainingProgress = 1f - currentProgress
            val currentRemainingDuration = (totalDuration * remainingProgress).toInt()

            animatable.animateTo(
                targetValue = 1f,
                animationSpec = tween(
                    durationMillis = if (isInitialRun) currentRemainingDuration else totalDuration,
                    easing = LinearEasing
                )
            )

            isInitialRun = false
            animatable.snapTo(0f)
        }
    }

    LaunchedEffect(rippleAnimatables) {
        rippleAnimatables.forEach { animatable ->
            launch { runContinuousLoop(animatable) }
        }
    }

    // ender Layout
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        rippleAnimatables.forEach { progressAnimatable ->
            val progress = progressAnimatable.value

            Box(
                modifier = Modifier
                    .requiredSize(circleSize)
                    .graphicsLayer {
                        scaleX = progress * maxScaleMultiplier
                        scaleY = progress * maxScaleMultiplier
                        alpha = peakAlpha * (1f - progress)
                    }
                    .background(color = circleColor, shape = CircleShape)
            )
        }

        content()
    }
}