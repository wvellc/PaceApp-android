package com.wvelabs.core_ui.alerts

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.EaseInOut
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarData
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import com.wvelabs.core_ui.extensions.defaultAnimSpec
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.abs

// ==========================================
// ALERT RENDERER (Collector)
// ==========================================

/**
 * The root container that observes the global alert stream and draws overlays (Toasts, Snackbars, Dialogs).
 *
 * 📘 ARCHITECTURE NOTE:
 * This is the "Collector" part of the alert system. It listens to events sent via [AppAlerts].
 * For this system to work, this container MUST wrap your NavHost in MainActivity.kt.
 *
 * 🏗️ SETUP (MainActivity.kt)
 * ```kotlin
 * setContent {
 *    PaceAppTheme {               // App Theme
 *        AppAlertContainer {      // Alert Container
 *            AppNavHost(...)      // Navigation Host
 *        }
 *    }
 * }
 * ```
 *
 * 🎯 TRIGGERING ALERTS
 * To show an alert, use the [AppAlerts] singleton from any ViewModel or Repository:
 * ```kotlin
 * AppAlerts.showToast("Operation successful")
 * ```
 *
 * @param toastContent UI for Toasts.
 * @param snackbarContent UI for Snack bars.
 * @param dialogContent UI for Dialogs.
 * @param content The main application UI (usually the NavHost).
 */

@Composable
fun AppAlertContainer(
    modifier: Modifier = Modifier,
    toastContent: @Composable (AlertType.Toast, onDismiss: () -> Unit) -> Unit = { toast, _ ->
        DefaultToast(toast)
    },
    snackbarContent: @Composable (SnackbarData) -> Unit = { data -> DefaultSnackbar(data) },
    dialogContent: @Composable (AlertType.Dialog, onDismiss: () -> Unit) -> Unit = { msg, dismiss ->
        DefaultDialog(msg, dismiss)
    },
    content: @Composable () -> Unit
) {
    val snackbarState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    var isSwipingToDismiss by remember { mutableStateOf(false) }
    var activeDialog by remember { mutableStateOf<AlertType.Dialog?>(null) }
    var activeToast by remember { mutableStateOf<AlertType.Toast?>(null) }
    var lastActiveToast by remember { mutableStateOf<AlertType.Toast?>(null) }

    // The Animatable state for the swipe-to-dismiss gesture
    val dragOffsetY = remember { Animatable(0f) }

    if (activeToast != null) {
        lastActiveToast = activeToast
    }

    val currentGravity = lastActiveToast?.gravity ?: ToastGravity.Bottom

    val alignment = when (currentGravity) {
        ToastGravity.Top -> Alignment.TopCenter
        ToastGravity.Center -> Alignment.Center
        ToastGravity.Bottom -> Alignment.BottomCenter
    }

    val enterDuration = 300
    val exitDuration = 300

    val enterAnimation = slideInVertically(
        animationSpec = defaultAnimSpec(enterDuration, easing = EaseInOut),
        initialOffsetY = { fullHeight ->
            if (currentGravity == ToastGravity.Top) -fullHeight else fullHeight
        }
    ) + fadeIn(
        animationSpec = defaultAnimSpec(enterDuration, easing = EaseInOut)
    )

    val exitAnimation = if (isSwipingToDismiss) {
        fadeOut(animationSpec = defaultAnimSpec(150)) // Fast fade out only
    } else {
        fadeOut(animationSpec = defaultAnimSpec(exitDuration, easing = EaseInOut))
    }

    // Reset everything when a new toast arrives
    LaunchedEffect(activeToast?.id) {
        if (activeToast != null) {
            isSwipingToDismiss = false
            dragOffsetY.snapTo(0f)
        }
    }
    // Observe global alert stream
    LaunchedEffect(Unit) {
        AppAlerts.alerts.collect { alert ->
            when (alert) {
                is AlertType.Toast -> {
                    activeToast = alert
                    // Instantly reset the drag offset when a new toast arrives!
                    dragOffsetY.snapTo(0f)

                    scope.launch {
                        delay(alert.durationMillis)
                        if (activeToast?.id == alert.id) activeToast = null
                    }
                }

                is AlertType.Snackbar -> {
                    scope.launch {
                        val result = snackbarState.showSnackbar(
                            AlertSnackbarVisuals(
                                message = alert.text,
                                actionLabel = alert.actionLabel,
                                withDismissAction = false,
                                duration = SnackbarDuration.Short,
                                type = alert.type
                            )
                        )
                        if (result == SnackbarResult.ActionPerformed) {
                            alert.onAction?.invoke()
                        }
                    }
                }

                is AlertType.Dialog -> {
                    activeDialog = alert
                }
            }
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = Color.Transparent,
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        snackbarHost = {
            SnackbarHost(snackbarState) { data ->
                snackbarContent(data)
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            // Main application content
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                content()
            }

            // Toast overlay
            AnimatedVisibility(
                visible = activeToast != null,
                enter = enterAnimation,
                exit = exitAnimation,
                modifier = Modifier
                    .align(alignment)
                    // Use safe padding only as a margin for the STARTING position
                    .windowInsetsPadding(WindowInsets.systemBars)
                    .padding(16.dp)
            ) {
                lastActiveToast?.let { toastAlert ->

                    // We apply the gesture modifier to a child Box inside AnimatedVisibility
                    // to ensure the drag offset doesn't conflict with the slide entry/exit animations.
                    Box(
                        modifier = Modifier
                            .graphicsLayer {
                                translationY = dragOffsetY.value
                                clip = false
                                // Fade out manually based on drag distance for extra polish
                                val scale = (1f - abs(dragOffsetY.value) / 2000f).coerceIn(0.9f, 1f)
                                scaleX = scale
                                scaleY = scale
                                alpha = (1f - abs(dragOffsetY.value) / 300f).coerceIn(0f, 1f)
                            }
                            .pointerInput(currentGravity) {
                                detectVerticalDragGestures(
                                    onVerticalDrag = { change, dragAmount ->
                                        change.consume()
                                        val newOffset = dragOffsetY.value + dragAmount

                                        // Swipe constraint
                                        val isValidDrag = when (currentGravity) {
                                            ToastGravity.Top -> newOffset < 0
                                            ToastGravity.Bottom -> newOffset > 0
                                            else -> true
                                        }
                                        if (isValidDrag) {
                                            scope.launch { dragOffsetY.snapTo(newOffset) }
                                        }
                                    },
                                    onDragEnd = {
                                        val threshold = 50f
                                        val shouldDismiss = when (currentGravity) {
                                            ToastGravity.Top -> dragOffsetY.value < -threshold
                                            ToastGravity.Bottom -> dragOffsetY.value > threshold
                                            else -> abs(dragOffsetY.value) > threshold
                                        }

                                        if (shouldDismiss) {
                                            scope.launch {
                                                // Manual "Flick" off-screen
                                                val target = when {
                                                    dragOffsetY.value < 0 -> -600f
                                                    else -> 600f
                                                }
                                                dragOffsetY.animateTo(
                                                    target,
                                                    spring(stiffness = Spring.StiffnessMedium)
                                                )

                                                //  ONLY THEN hide the toast to prevent the "jump" crash
                                                activeToast = null
                                            }
                                        } else {
                                            scope.launch {
                                                dragOffsetY.animateTo(0f, spring())
                                            }
                                        }
                                    }
                                )
                            }
                    ) {
                        toastContent(toastAlert) {
                            activeToast = null
                        }
                    }
                }
            }
        }
    }

    // Dialog overlay
    activeDialog?.let { dialog ->
        dialogContent(dialog) { activeDialog = null }
    }
}