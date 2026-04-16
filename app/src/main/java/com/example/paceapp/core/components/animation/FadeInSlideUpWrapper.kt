package com.example.paceapp.core.components.animation

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.runtime.Composable

@Composable
fun FadeInSlideUpWrapper(
    content: @Composable AnimatedVisibilityScope.() -> Unit
) = AnimationWrapper(
    content = content,
    enter = fadeIn(animationSpec = tween(durationMillis = 500)) +
            slideInVertically(
                initialOffsetY = { fullHeight -> fullHeight / 10 }, // Start slightly lower
                animationSpec = tween(durationMillis = 500)
            ),

    )