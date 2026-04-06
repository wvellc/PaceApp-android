package com.wvelabs.core_ui.shell

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import com.wvelabs.core_ui.base.ViewState
import com.wvelabs.core_ui.components.AppLoader

@Composable
fun AppScreenBox(
    state: ViewState,
    animationWrapper: @Composable (content: @Composable () -> Unit) -> Unit = { it() },
    background: @Composable () -> Unit = {},
    appBar: @Composable () -> Unit = {},
    isScrollable: Boolean = false, // Toggle scrolling globally
    content: @Composable () -> Unit,
    loader: @Composable () -> Unit = { AppLoader() },
) {
    Box(modifier = Modifier.fillMaxSize()) {
        // 1. Background Slot (Stays at the very bottom)
        background()

        // 2. Animated Column for UI Stacking
        animationWrapper {
            Column(modifier = Modifier.fillMaxSize()) {
                // Optional App Bar at the Top
                appBar()

                // Content area that respects the App Bar's height
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .then(
                            if (isScrollable) Modifier.verticalScroll(rememberScrollState())
                            else Modifier
                        )
                ) {
                    content()
                }
            }
        }
    }
}