package com.wvelabs.core_ui.components.liquidtabbar

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import com.kyant.capsule.ContinuousCapsule

val LocalLiquidBottomTabScale = staticCompositionLocalOf { { 1f } }

@Composable
fun LiquidTabItem(
    modifier: Modifier = Modifier,
    isSelected: Boolean,
    selectedColor: Color,
    shape: Shape = ContinuousCapsule,
    content: @Composable BoxScope.() -> Unit
) {
    val scaleProvider = LocalLiquidBottomTabScale.current

    Box(
        modifier = modifier
            .fillMaxHeight()
            .clip(shape)
            .background(if (isSelected) selectedColor else Color.Transparent)
            .graphicsLayer {
                val currentScale = scaleProvider()
                scaleX = currentScale
                scaleY = currentScale
            },
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier,
            contentAlignment = Alignment.Center,
            content = content
        )
    }
}