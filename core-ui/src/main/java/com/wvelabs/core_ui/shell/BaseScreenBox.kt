package com.wvelabs.core_ui.shell

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

@Composable
fun BaseScreenBox(
    modifier: Modifier,
    animationWrapper: @Composable (content: @Composable () -> Unit) -> Unit = { it() },
    background: @Composable () -> Unit = {},
    appBar: @Composable () -> Unit = {},
    applySystemInsets: Boolean = true,
    content: @Composable ((innerPaddings: PaddingValues) -> Unit),
) {
    Box(
        modifier = Modifier.fillMaxSize(),
    ) {
        // Background Slot (Stays at the very bottom)
        background()

        // Animated Column for UI Stacking
        Scaffold(
            modifier = modifier,
            containerColor = Color.Transparent, //Set to transparent so your custom background() shows through!
            contentWindowInsets = if (applySystemInsets) WindowInsets.systemBars else WindowInsets(
                0,
                0,
                0,
                0
            ),
            topBar = {
                Box(
                    modifier = when {
                        applySystemInsets -> Modifier.statusBarsPadding()
                        else -> Modifier
                    }
                ) {
                    appBar()
                }
            },
            content = { padding ->
                animationWrapper {
                    content(padding)
                }
            },
        )
    }
}