package com.wvelabs.core_ui.components.dropdown

// ⚠️ Removed the 'clip' import!
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import com.kyant.backdrop.Backdrop
import com.kyant.backdrop.drawBackdrop
import com.kyant.backdrop.effects.blur
import com.kyant.backdrop.effects.lens
import com.kyant.backdrop.effects.opacity
import com.kyant.backdrop.effects.vibrancy
import com.kyant.backdrop.shadow.InnerShadow
import com.kyant.backdrop.shadow.Shadow

/**
 * Encapsulates all Backdrop and Liquid Glass aesthetics so they don't pollute the UI tree.
 */
internal fun Modifier.liquidGlassPopup(
    backdrop: Backdrop,
    menuCornerRadius: Dp,
    menuBackgroundColor: Color,
    menuElevation: Dp,
    shouldShowAbove: Boolean
): Modifier {
    val menuShape = RoundedCornerShape(menuCornerRadius)
    return this
        .graphicsLayer {
            transformOrigin = TransformOrigin(0.5f, if (shouldShowAbove) 1f else 0f)
            // Note: We DO NOT enable clip = true here either!
        }
        .drawBackdrop(
            backdrop = backdrop,
            shape = { menuShape }, // This tells the blur/lens to stay neatly inside the box
            effects = {
                opacity(0.9f)
                vibrancy()
                blur(6f.dp.toPx())
                lens(
                    refractionHeight = 32f.dp.toPx(),
                    refractionAmount = 64f.dp.toPx(),
                    chromaticAberration = true
                )
            },
            shadow = {
                Shadow(
                    alpha = 0.25f,
                    radius = menuElevation,
                    // Cast the shadow straight down slightly for 3D depth
                    offset = DpOffset(0.dp, menuElevation / 3f),
                    color = Color.Black
                )
            },
            innerShadow = { InnerShadow(radius = 8f.dp, alpha = 0.2f) },
            onDrawSurface = {
                drawRoundRect(
                    color = menuBackgroundColor,
                    cornerRadius = CornerRadius(menuCornerRadius.toPx())
                )
                // Retain glossy top reflection gradient
//                drawRoundRect(
//                    brush = Brush.verticalGradient(
//                        listOf(
//                            Color.White.copy(alpha = 0.25f),
//                            Color.White.copy(alpha = 0.1f),
//                            Color.Transparent
//                        )
//                    ),
//                    cornerRadius = CornerRadius(menuCornerRadius.toPx())
//                )
            }
        )
        .border(
            BorderStroke(
                1.dp,
                Brush.verticalGradient(
                    listOf(
                        Color.White.copy(alpha = 0.4f),
                        Color.White.copy(alpha = 0.1f)
                    )
                )
            ),
            menuShape
        )
}