package net.paceapp.core.components

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.EaseInOut
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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import net.paceapp.theme.AppColors

/**
 * A purely Jetpack Compose-driven overlay dialog.
 * Bypasses the native Android Window Manager to allow independent, flawless
 * entry and exit animations without canvas invalidation bugs.
 *
 * @param onDismissRequest Triggered when the exit animation is 100% complete.
 * @param modifier Applied to the inner dialog content wrapper.
 * @param cancelable Whether tapping outside or pressing the back button dismisses the dialog.
 * @param overlayColor The background scrim color.
 * @param duration The primary entry animation duration.
 * @param content The UI to display. Provides a `dismiss` lambda to trigger the exit animation.
 */
@Composable
fun AppDialogOverlay(
    onDismissRequest: () -> Unit,
    cancelable: Boolean = true,
    overlayColor: Color = AppColors.Black40,
    duration: Int = 500,
    content: @Composable (dismiss: () -> Unit) -> Unit
) {
    // Animation States
    val entryAnimationSpec = tween<Float>(duration)
    val exitDuration = 300

    val transitionState = remember {
        MutableTransitionState(initialState = false).apply {
            targetState = true // Instantly target true to trigger the Entry animation
        }
    }

    // The Semantic Dismiss Trigger
    val dismiss = {
        transitionState.targetState = false
    }

    // Wait for exit animation to finish before destroying the UI
    LaunchedEffect(transitionState.currentState, transitionState.isIdle) {
        if (!transitionState.currentState && transitionState.isIdle) {
            onDismissRequest()
        }
    }

    // Hardware Back Button Interception
    BackHandler(enabled = transitionState.targetState && cancelable) {
        dismiss()
    }

    // Overlay Animation
    val animatedAlpha by animateFloatAsState(
        targetValue = if (transitionState.targetState) 1f else 0f,
        animationSpec = tween(
            durationMillis = if (transitionState.targetState) duration else exitDuration,
            easing = EaseInOut
        ),
        label = "OverlayAlpha"
    )

    // Overlay Container
    Box(
        modifier = Modifier
            .fillMaxSize()
            .drawBehind {
                drawRect(color = overlayColor.copy(alpha = overlayColor.alpha * animatedAlpha))
            }
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = { if (cancelable) dismiss() }
                )
            },
        contentAlignment = Alignment.Center
    ) {
        // Dialog Card Animation
        AnimatedVisibility(
            visibleState = transitionState,
            enter = fadeIn(tween(200)),
            exit = fadeOut(tween(exitDuration)),
            modifier = Modifier.wrapContentSize().pointerInput(Unit) { detectTapGestures {} } // Prevent click-through to background
        ) {
            Box(
                modifier = Modifier
                    .animateEnterExit(
                        enter = scaleIn(
                            animationSpec = entryAnimationSpec,
                            initialScale = 0.5f
                        ),
                        exit = scaleOut(
                            animationSpec = tween(exitDuration),
                            targetScale = 0.7f
                        )
                    )
            ) {
                content(dismiss)
            }
        }
    }
}