package net.paceapp.core.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import net.paceapp.R
import net.paceapp.theme.AppColors
import com.wvelabs.core_ui.shell.BaseScreenBox

@Composable
fun AppBaseScreen(
    modifier: Modifier = Modifier,
    backgroundModifier: Modifier = Modifier,
    animationWrapper: @Composable (content: @Composable () -> Unit) -> Unit = { content -> content() },
    isLoading: Boolean = false,
    customBackground: (@Composable () -> Unit)? = null,
    hasPattern: Boolean = false,
    appBar: @Composable () -> Unit = {},
    applySystemInsets: Boolean = true,
    appLoader: @Composable () -> Unit = { AppLoadingIndicator() },
    content: @Composable ((innerPaddings: PaddingValues) -> Unit),
) {

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        BaseScreenBox(
            animationWrapper = animationWrapper,
            appBar = appBar,
            modifier = modifier,
            content = content,
            applySystemInsets = applySystemInsets,
            background = {
                Box(
                    modifier = backgroundModifier
                        .fillMaxSize()
                        .background(
                            brush = Brush.verticalGradient(
                                AppColors.backgroundGradient,
                            )
                        )
                ) {
                    if (customBackground != null) {
                        customBackground()
                    }

                    if (hasPattern) {
                        Image(
                            modifier = Modifier
                                .fillMaxSize(),
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
            appLoader()
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