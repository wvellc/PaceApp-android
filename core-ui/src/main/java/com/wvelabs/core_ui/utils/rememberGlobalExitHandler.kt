package com.wvelabs.core_ui.utils

import android.app.Activity
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.delay

@Composable
fun rememberGlobalExitHandler(
    message: String = "Tap again to exit",
    delayMillis: Long = 2000L
): () -> Unit { // Returns a lambda that we can trigger manually!
    
    val context = LocalContext.current
    var backPressedOnce by remember { mutableStateOf(false) }

    // 1. The Timer
    LaunchedEffect(backPressedOnce) {
        if (backPressedOnce) {
            delay(delayMillis)
            backPressedOnce = false
        }
    }

    // 2. The Logic
    val triggerExitAttempt: () -> Unit = remember(context) {
        {
            if (backPressedOnce) {
                (context as? Activity)?.finish()
            } else {
                backPressedOnce = true
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    // 3. System Back Swipe Interceptor
    // NavHost consumes back presses when it has history. 
    // This only fires when the NavHost is completely empty!
    BackHandler(enabled = true) {
        triggerExitAttempt()
    }

    return triggerExitAttempt
}