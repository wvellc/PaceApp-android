package com.wvelabs.core_ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp

@Composable
fun AppLoader(
    modifier: Modifier = Modifier,
    color: Color = Color.White,
    backgroundTint: Color? = null,
    // Optional slot for Lottie or custom PNG/GIF animations
    customLoader: @Composable (() -> Unit)? = null,
    isFullScreen: Boolean = false
) {
    val containerModifier = if (isFullScreen) {
        Modifier
            .fillMaxSize()
            .background(backgroundTint ?: Color.Black.copy(alpha = 0.4f))
            .pointerInput(Unit) {} // Block touches
            .clickable(enabled = false) {} // Extra safety
    } else {
        Modifier.wrapContentSize() // Only takes space of the loader itself
    }
    Box(
        modifier = containerModifier,
    ) {
        if (customLoader != null) {
            customLoader()
        } else {
            // Default Proxy: Easy to swap globally
            CircularProgressIndicator(
                modifier = modifier.size(48.dp),
                color = color,
                strokeWidth = 4.dp
            )
        }
    }

}