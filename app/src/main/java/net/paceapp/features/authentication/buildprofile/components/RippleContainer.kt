package net.paceapp.features.authentication.buildprofile.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

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
    val totalDuration = remember(rippleCount, staggerDelayMillis) {
        (rippleCount * staggerDelayMillis).toInt()
    }

    // Create a single infinite transition bound to the Compose lifecycle
    val infiniteTransition = rememberInfiniteTransition(label = "Ripple")

    // Create one master clock that smoothly ticks from 0 to totalDuration
    val timeClock by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = totalDuration.toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = totalDuration, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "RippleClock"
    )

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        // Render each circle based purely on math, never coroutines
        for (index in 0 until rippleCount) {
            // Shift the time forward for each circle based on its index
            val virtualTime = (timeClock + (index * staggerDelayMillis)) % totalDuration
            val progress = virtualTime / totalDuration

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