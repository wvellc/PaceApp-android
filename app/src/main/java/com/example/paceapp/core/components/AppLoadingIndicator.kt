package com.example.paceapp.core.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.example.paceapp.theme.AppColors
import com.wvelabs.core_ui.components.BaseLoader

@Composable
fun AppLoadingIndicator(
    modifier: Modifier = Modifier,
    isFullScreen: Boolean = true,
    backgroundColor: Color = AppColors.NeonAquaBlue.copy(alpha = 0.5f),
    loaderColor: Color = AppColors.White
) {
    BaseLoader(
        modifier = modifier,
        isFullScreen = isFullScreen,
        color = loaderColor,
        backgroundTint = backgroundColor,
        customLoader = null
    )
}