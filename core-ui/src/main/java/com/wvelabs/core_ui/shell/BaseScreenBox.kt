package com.wvelabs.core_ui.shell

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
fun BaseScreenBox(
    modifier: Modifier,
    animationWrapper: @Composable (content: @Composable () -> Unit) -> Unit = { it() },
    background: @Composable () -> Unit = {},
    appBar: @Composable () -> Unit = {},
    isScrollable: Boolean = false, // Toggle scrolling globally
    contentAlignment: Alignment = Alignment.TopCenter,
    content: @Composable () -> Unit,
) {
    Box(
        modifier = modifier,
        contentAlignment = contentAlignment
    ) {
        // Background Slot (Stays at the very bottom)
        background()

        // Animated Column for UI Stacking
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