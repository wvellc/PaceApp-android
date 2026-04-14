package com.example.paceapp.core.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

@Composable
fun FadeInSlideUpWrapper(
    content: @Composable () -> Unit
) {
    // State to trigger the animation
    var isVisible by remember { mutableStateOf(false) }

    // Trigger it exactly once when the screen first composes
    LaunchedEffect(Unit) {
        isVisible = true
    }

    AnimatedVisibility(
        visible = isVisible,
        enter = fadeIn(animationSpec = tween(durationMillis = 500)) +
                slideInVertically(
                    initialOffsetY = { fullHeight -> fullHeight / 10 }, // Start slightly lower
                    animationSpec = tween(durationMillis = 500)
                )
    ) {
        // Yield to the Scaffold/Content
        content()
    }
}