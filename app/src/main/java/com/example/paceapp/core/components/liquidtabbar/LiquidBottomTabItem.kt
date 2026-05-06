package com.example.paceapp.core.components.liquidtabbar

import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.graphicsLayer
import com.example.paceapp.core.extensions.defaultClickable
import com.kyant.capsule.ContinuousCapsule

val LocalLiquidBottomTabScale = staticCompositionLocalOf { { 1f } }

@Composable
fun RowScope.LiquidTabItem(
    modifier: Modifier = Modifier,
    isSelected: Boolean,
    selectedColor: Color,
    onClick: () -> Unit = {},
    shape: Shape = ContinuousCapsule,
    content: @Composable () -> Unit
) {
    val scaleProvider = LocalLiquidBottomTabScale.current

    Box(
        modifier = modifier
            .weight(
                when {
                    isSelected -> 1.2f
                    else -> 1f
                }
            )
            .fillMaxHeight()
            .clip(shape)
            .background(if (isSelected) selectedColor else Color.Transparent)
            .defaultClickable(
                interactionSource = remember { MutableInteractionSource() },
                onClick = onClick
            )
            .graphicsLayer {
                val currentScale = scaleProvider()
                scaleX = currentScale
                scaleY = currentScale
            },
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier.wrapContentWidth(unbounded = true),
            contentAlignment = Alignment.Center
        ) {
            content()
        }
    }
}