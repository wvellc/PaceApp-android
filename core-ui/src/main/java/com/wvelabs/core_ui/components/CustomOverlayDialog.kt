package com.wvelabs.core_ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import kotlinx.coroutines.launch

@Composable
fun CustomOverlayDialog(
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    overlayColor: Color = Color.Black.copy(alpha = 0.6f),
    properties: DialogProperties = DialogProperties(
        usePlatformDefaultWidth = false,
        decorFitsSystemWindows = false
    ),
    content: @Composable () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    // Animate the background overlay
    val animatedAlpha = remember { Animatable(0f) }
    var isClosing by remember { mutableStateOf(false) }
    LaunchedEffect(coroutineScope, animatedAlpha) {
        coroutineScope.launch {
            animatedAlpha.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = 300)
            )
        }
    }
    LaunchedEffect(isClosing) {
        if (isClosing) {
            coroutineScope.launch {
                animatedAlpha.snapTo(0f)
            }
            onDismissRequest()
            isClosing = false
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .drawBehind {
                drawRect(
                    color = overlayColor.copy(alpha = 0.4f * animatedAlpha.value)
                )
            },
        contentAlignment = Alignment.Center
    ) {

        // The Dialog
        Dialog(
            onDismissRequest = {
                isClosing = true
            },
            properties = properties,
            content = content
        )
    }
}