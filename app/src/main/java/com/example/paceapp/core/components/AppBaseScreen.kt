package com.example.paceapp.core.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.wvelabs.core_ui.shell.BaseScreenBox

@Composable
fun AppBaseScreen(
    modifier: Modifier = Modifier,
    animationWrapper: @Composable (content: @Composable () -> Unit) -> Unit = { content -> content() },
    isLoading: Boolean = false,
    background: @Composable () -> Unit = {},
    appBar: @Composable () -> Unit = {},
    contentAlignment: Alignment = Alignment.TopCenter,
    content: @Composable () -> Unit,
) {
    BaseScreenBox(
        animationWrapper = animationWrapper,
        background = background,
        appBar = appBar,
        modifier = modifier,
        contentAlignment = contentAlignment,
    ) {

        content()

        // Global Loading Overlay
        if (isLoading) {
            AppLoadingIndicator()
        }
    }
}

