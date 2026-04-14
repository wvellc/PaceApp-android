package com.example.paceapp.core.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import com.example.paceapp.R
import com.example.paceapp.ui.theme.AppColors
import com.wvelabs.core_ui.shell.BaseScreenBox

@Composable
fun AppBaseScreen(
    modifier: Modifier = Modifier,
    animationWrapper: @Composable (content: @Composable () -> Unit) -> Unit = { content -> content() },
    isLoading: Boolean = false,
    customBackground: @Composable () -> Unit = {},
    hasPattern: Boolean = false,
    appBar: @Composable () -> Unit = {},
    content: @Composable ((innerPaddings: PaddingValues) -> Unit),
) {
    Box(modifier = Modifier.fillMaxSize()) {
        BaseScreenBox(
            animationWrapper = animationWrapper,
            appBar = appBar,
            modifier = modifier,
            content = content,
            background = {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            brush = Brush.verticalGradient(
                                AppColors.backgroundGradient,
                            )
                        )
                ) {
                    customBackground()

                    if (hasPattern) {
                        Image(
                            modifier = Modifier.fillMaxSize(),
                            painter = painterResource(R.drawable.bg_star_pattern),
                            contentDescription = null,
                            contentScale = ContentScale.Crop
                        )
                    }

                }
            }
        )
        // Global Loading Overlay
        if (isLoading) {
            AppLoadingIndicator()
        }
    }
}


@Preview
@Composable
fun AppBaseScreenPreview() = AppBaseScreen(
    modifier = Modifier.fillMaxSize(),
    isLoading = false,
    hasPattern = true,
) {}