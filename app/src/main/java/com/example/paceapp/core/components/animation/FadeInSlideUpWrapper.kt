package com.example.paceapp.core.components.animation

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.core.EaseInOut
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.runtime.Composable
import com.wvelabs.core_ui.extensions.defaultAnimSpec

val fadeInUpTransition = fadeIn(animationSpec = defaultAnimSpec(delay = 500)) +
        slideInVertically(
            initialOffsetY = { height -> height / 2 },
            animationSpec = defaultAnimSpec(
                delay = 500,
                easing = EaseInOut
            )
        )

@Composable
fun FadeInUpWrapper(
    content: @Composable AnimatedVisibilityScope.() -> Unit
) = AnimationWrapper(
    content = content,
    enter = fadeInUpTransition,
)