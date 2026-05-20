package com.wvelabs.core_ui.components

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput

@Composable
fun PureComposeOverlayDialog(
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    cancelable: Boolean = true,
    overlayColor: Color = Color.Black.copy(alpha = 0.6f),
    animationDuration: Int = 500,
    content: @Composable (triggerDismiss: () -> Unit) -> Unit
) {
    // 1. THE FIX: MutableTransitionState
    // Start at false, immediately target true. This forces the Entry Animation!
    val transitionState = remember {
        MutableTransitionState(initialState = false).apply {
            targetState = true
        }
    }

    // 2. The function that triggers the Exit Animation
    val triggerExitAnimation = {
        transitionState.targetState = false
    }

    // 3. Listen to the animation state natively.
    // When the exit animation is 100% finished, tell the parent to destroy the dialog.
    LaunchedEffect(transitionState.currentState, transitionState.isIdle) {
        if (!transitionState.currentState && transitionState.isIdle) {
            onDismissRequest()
        }
    }

    // Intercept physical Android back button
    BackHandler(enabled = transitionState.targetState && cancelable) {
        triggerExitAnimation()
    }

    // Animate the background overlay alpha
    val animatedAlpha by animateFloatAsState(
        targetValue = if (transitionState.targetState) 1f else 0f,
        animationSpec = tween(animationDuration),
        label = "LayerAlpha"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .drawBehind {
                drawRect(color = overlayColor.copy(alpha = overlayColor.alpha * animatedAlpha))
            }
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = { if (cancelable) triggerExitAnimation() }
                )
            },
        contentAlignment = Alignment.Center
    ) {
        // 4. Pass the transitionState directly into AnimatedVisibility
        AnimatedVisibility(
            visibleState = transitionState,
            enter = fadeIn(tween(animationDuration)) + scaleIn(
                initialScale = 0.5f,
                animationSpec = tween(animationDuration)
            ),
            exit = fadeOut(tween(animationDuration)) + scaleOut(
                targetScale = 0.5f,
                animationSpec = tween(animationDuration)
            ),
            modifier = Modifier.pointerInput(Unit) { detectTapGestures {} } // Prevent click-through
        ) {
            Box(modifier = modifier) {
                content(triggerExitAnimation)
            }
        }
    }
}