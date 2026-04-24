package com.example.paceapp.core.components.animation

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.core.EaseInOut
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.runtime.Composable
import com.wvelabs.core_ui.extensions.defaultAnimSpec

fun fadeInUpTransition(
    from: Float = 1f,
    delay: Int = 0,
    duration: Int = 500,
) = fadeIn(animationSpec = defaultAnimSpec(delay = delay, duration = duration)) +
        slideInVertically(
            initialOffsetY = { height -> (height * from).toInt() },
            animationSpec = defaultAnimSpec(
                delay = delay + 100,// Slight offset from fade for that "layered" feel
                duration = duration,
                easing = EaseInOut
            )
        )

@Composable
fun FadeInUpWrapper(
    initialOffsetFraction: Float = 0.5f,
    enterTransition: EnterTransition = fadeInUpTransition(from = initialOffsetFraction),
    content: @Composable AnimatedVisibilityScope.() -> Unit
) = AnimationWrapper(
    content = content,
    enter = enterTransition,
)