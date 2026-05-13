package com.wvelabs.core_ui.components

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import com.kyant.backdrop.backdrops.LayerBackdrop
import com.kyant.backdrop.drawBackdrop
import com.kyant.backdrop.effects.blur
import com.kyant.backdrop.effects.lens
import com.kyant.backdrop.effects.vibrancy
import com.kyant.backdrop.shadow.InnerShadow
import com.kyant.capsule.ContinuousRoundedRectangle
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun LiquidGlassButton(
    onClick: () -> Unit,
    backdrop: LayerBackdrop,
    modifier: Modifier = Modifier,
    shape: Shape = ContinuousRoundedRectangle(24.dp),
    blurRadius: Dp = 8.dp,
    refractionHeight: Dp = 8.dp,
    refractionAmount: Dp = 12.dp,
    tintColor: Color = Color.Black,
    content: @Composable BoxScope.() -> Unit
) {
    var isPressed by remember { mutableStateOf(false) }

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 1.12f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "glass_button_scale"
    )

    val tintAlpha by animateFloatAsState(
        targetValue = if (isPressed) 0.3f else 0.0f,
        animationSpec = tween(durationMillis = 150),
        label = "glass_button_tint"
    )

    Box(
        modifier = modifier
            // Hardware layer and bouncy scale
            .graphicsLayer {
                this.scaleX = scale
                this.scaleY = scale
                this.shape = shape
                this.clip = true
            }
            // Instant Touch Detection
            .pointerInput(Unit) {
                awaitEachGesture {
                    awaitFirstDown(requireUnconsumed = false)
                    isPressed = true
                    waitForUpOrCancellation()
                    isPressed = false
                }
            }
            // The Glass Engine
            .drawBackdrop(
                backdrop = backdrop,
                shape = { shape },
                effects = {
                    vibrancy()
                    blur(blurRadius.toPx())
                    lens(
                        refractionHeight = refractionHeight.toPx(),
                        refractionAmount = refractionAmount.toPx(),
                        chromaticAberration = true
                    )
                },
                layerBlock = {
                    this.shape = shape
                    this.clip = true
                },
                innerShadow = {
                    val radians = 45.0 * (Math.PI / 180.0)
                    val distance = 2.dp

                    val offsetX = distance * cos(radians).toFloat()
                    val offsetY = distance * sin(radians).toFloat()

                    InnerShadow(
                        radius = blurRadius / 2,
                        alpha = 0.8f,
                        offset = DpOffset(x = offsetX, y = offsetY),
                        color = Color.White
                    )
                },

                onDrawSurface = {
                    drawRect(color = tintColor, alpha = tintAlpha)
                }
            )

            // Accessible Click Handler
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            ),
        contentAlignment = Alignment.Center,
        content = content
    )
}