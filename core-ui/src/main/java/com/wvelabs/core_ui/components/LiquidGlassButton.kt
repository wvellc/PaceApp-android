package com.wvelabs.core_ui.components

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.BoxWithConstraints
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
import androidx.compose.ui.graphics.drawOutline
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.times
import com.kyant.backdrop.Backdrop
import com.kyant.backdrop.drawBackdrop
import com.kyant.backdrop.effects.blur
import com.kyant.backdrop.effects.lens
import com.kyant.backdrop.effects.opacity
import com.kyant.backdrop.effects.vibrancy
import com.kyant.backdrop.highlight.Highlight
import com.kyant.backdrop.shadow.Shadow
import com.kyant.capsule.ContinuousRoundedRectangle

@Composable
fun LiquidGlassButton(
    onClick: () -> Unit,
    backdrop: Backdrop,
    modifier: Modifier = Modifier,
    shape: Shape = ContinuousRoundedRectangle(24.dp),
    lightAngle: Float = -45f,
    lightIntensity: Float = 80f,
    refraction: Float = 67f,
    depth: Float = 44f,
    frost: Float = 42f,
    splay: Float = 48f,
    dispersion: Boolean = true,
    maxScale: Float = 1.15f,
    content: @Composable BoxScope.() -> Unit
) {
    var isPressed by remember { mutableStateOf(false) }

    val scale by animateFloatAsState(
        targetValue = if (isPressed) maxScale else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "glass_button_scale"
    )

    val tintAlpha by animateFloatAsState(
        targetValue = if (isPressed) 0.3f else 0.05f,
        animationSpec = tween(durationMillis = 150),
        label = "glass_button_tint"
    )

    BoxWithConstraints(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {

        val minDimension = if (maxWidth < maxHeight) maxWidth else maxHeight


        val actualBlur = (frost / 100f) * minDimension
        val actualRefraction = (refraction / 100f) * minDimension
        val actualDepth = (depth / 100f) * minDimension
        val actualSplay = (splay / 100f) * minDimension
        val actualAlpha = (lightIntensity / 100f)

        // Apply the calculated effects to an inner Box filling the constraints
        Box(
            modifier = Modifier
                .clearAndSetSemantics {}
                .matchParentSize()
                .graphicsLayer {
                    this.scaleX = scale
                    this.scaleY = scale
                    this.shape = shape
                    this.clip = true
                }
                .drawBackdrop(
                    backdrop = backdrop,
                    shape = { shape },
                    highlight = { Highlight.Default },
                    effects = {
                        opacity(0.65f)
                        vibrancy()

                        blur(actualBlur.toPx())

                        lens(
                            refractionHeight = actualDepth.toPx(),
                            refractionAmount = actualRefraction.toPx(),
                            chromaticAberration = dispersion
                        )
                    },

                    layerBlock = {
                        this.shape = shape
                        this.clip = true
                    },
                    shadow = {
                        Shadow(
                            color = Color.Black.copy(alpha = 0.25f),
                            radius = 16.dp,
                            offset = DpOffset(0.dp, 8.dp) // Pushes shadow straight down
                        )
                    },
//                    innerShadow = {
//                        val radians = (lightAngle * -1) * (Math.PI / 180.0)
//                        val shadowDistance = actualSplay / 3
//
//                        val offsetX = shadowDistance * cos(radians).toFloat()
//                        val offsetY = shadowDistance * sin(radians).toFloat()
//
//                        InnerShadow(
//                            radius = actualSplay,
//                            alpha = actualAlpha,
//                            offset = DpOffset(x = -offsetX, y = -offsetY),
//                            color = Color.White.copy(alpha = 0.7f)
//                        )
//                    },
                    onDrawSurface = {
                        drawRect(
                            brush = Brush.linearGradient(
                                colors = listOf(
                                    Color.White.copy(alpha = tintAlpha + 0.1f), // Brighter top-left
                                    Color.White.copy(alpha = tintAlpha)         // Slight reflection bottom-right
                                )
                            )
                        )
                    },
                    onDrawFront = {
                        drawOutline(
                            outline = shape.createOutline(size, layoutDirection, this),
                            brush = Brush.linearGradient(
                                colors = listOf(
                                    Color.White.copy(alpha = 0.5f), // Bright highlight top-left
                                    Color.Transparent,              // No border in middle
                                    Color.White.copy(alpha = 0.1f)  // Tiny reflection bottom-right
                                )
                            ),
                            style = Stroke(width = 1.dp.toPx()) // 1dp thin glass edge
                        )
                    }
                )
                .pointerInput(Unit) {
                    awaitEachGesture {
                        awaitFirstDown(requireUnconsumed = false)
                        isPressed = true
                        waitForUpOrCancellation()
                        isPressed = false
                    }
                }
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = onClick
                ),
            contentAlignment = Alignment.Center,
            content = content
        )
    }
}