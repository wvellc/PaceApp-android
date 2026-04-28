package com.wvelabs.core_ui.utils

import android.app.Activity
import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import com.wvelabs.core_ui.alerts.AppAlerts
import com.wvelabs.core_ui.alerts.MessageType
import com.wvelabs.core_ui.alerts.ToastGravity
import kotlinx.coroutines.delay

@Composable
fun rememberGlobalExitHandler(
    message: String = "Tap again to exit",
    delayMillis: Long = 2000L
): () -> Unit { // Returns a lambda that we can trigger manually!

    val context = LocalContext.current
    var backPressedOnce by remember { mutableStateOf(false) }

    // The Timer
    LaunchedEffect(backPressedOnce) {
        if (backPressedOnce) {
            delay(delayMillis)
            backPressedOnce = false
        }
    }

    // The Logic
    val triggerExitAttempt: () -> Unit = remember(context) {
        {
            if (backPressedOnce) {
                (context as? Activity)?.finish()
            } else {
                backPressedOnce = true
                AppAlerts.showToast(
                    text = message,
                    gravity = ToastGravity.Top,
                    durationMillis = delayMillis,
                    type = MessageType.Info,
                )
            }
        }
    }

    // System Back Swipe Interceptor
    // NavHost consumes back presses when it has history. 
    // This only fires when the NavHost is completely empty!
    BackHandler(enabled = true) {
        triggerExitAttempt()
    }

    return triggerExitAttempt
}