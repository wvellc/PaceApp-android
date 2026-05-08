package com.wvelabs.core_ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastCoerceIn
import androidx.compose.ui.util.lerp
import com.kyant.backdrop.Backdrop
import com.kyant.backdrop.backdrops.layerBackdrop
import com.kyant.backdrop.backdrops.rememberBackdrop
import com.kyant.backdrop.backdrops.rememberCombinedBackdrop
import com.kyant.backdrop.backdrops.rememberLayerBackdrop
import com.kyant.backdrop.drawBackdrop
import com.kyant.backdrop.effects.blur
import com.kyant.backdrop.effects.lens
import com.kyant.backdrop.highlight.Highlight
import com.kyant.backdrop.shadow.InnerShadow
import com.kyant.backdrop.shadow.Shadow
import com.kyant.capsule.ContinuousCapsule
import com.wvelabs.core_ui.components.liquidtabbar.DampedDragAnimation
import kotlinx.coroutines.flow.collectLatest
@Composable
fun LiquidSwitch(
    selected: () -> Boolean,
    onSelect: (Boolean) -> Unit,
    backdrop: Backdrop,
    modifier: Modifier = Modifier,
    switchColor: Color = Color(0xFF34C759),
    trackColor: Color = Color(0xFF787878).copy(alpha = 0.2f),
    thumbColor: Color = Color.White,
    trackWidth: Dp = 64.dp,
    trackHeight: Dp = 28.dp,
    thumbWidth: Dp = 39.dp,
    thumbPadding: Dp = 2.dp
) {
    val thumbHeight = trackHeight - (thumbPadding * 2)
    val dragWidthDp = (trackWidth - thumbWidth - (thumbPadding * 2)).coerceAtLeast(0.dp)

    val density = LocalDensity.current
    val isLtr = LocalLayoutDirection.current == LayoutDirection.Ltr

    val dragWidthPx = with(density) { dragWidthDp.toPx() }
    val paddingPx = with(density) { thumbPadding.toPx() }
    val thumbHeightPx = with(density) { thumbHeight.toPx() }

    val blurRadiusPx = thumbHeightPx * (8f / 24f)
    val lensDistPx = thumbHeightPx * (5f / 24f)
    val lensScalePx = thumbHeightPx * (10f / 24f)
    val shadowRadiusDp = thumbHeight * (4f / 24f)

    val animationScope = rememberCoroutineScope()

    // NEW: Tracks if the user is physically touching the switch
    var isInteracting by remember { mutableStateOf(false) }
    var didDrag by remember { mutableStateOf(false) }
    var fraction by remember { mutableFloatStateOf(if (selected()) 1f else 0f) }

    val dampedDragAnimation = remember(animationScope, dragWidthPx, isLtr) {
        DampedDragAnimation(
            animationScope = animationScope,
            initialValue = fraction,
            valueRange = 0f..1f,
            visibilityThreshold = 0.001f,
            initialScale = 1f,
            pressedScale = 1.5f,
            onDragStarted = {
                isInteracting = true // Finger is down
            },
            onDragStopped = {
                isInteracting = false // Finger is lifted

                if (didDrag) {
                    fraction = if (targetValue >= 0.5f) 1f else 0f
                    onSelect(fraction == 1f)
                    didDrag = false
                } else {
                    // It was a direct tap on the switch!
                    // Update state, the LaunchedEffect will handle the animation.
                    val newTarget = if (selected()) 0f else 1f
                    onSelect(newTarget == 1f)
                }
            },
            onDrag = { _, dragAmount ->
                if (!didDrag) {
                    didDrag = dragAmount.x != 0f
                }
                val delta = if (dragWidthPx > 0) dragAmount.x / dragWidthPx else 0f
                fraction =
                    if (isLtr) (fraction + delta).fastCoerceIn(0f, 1f)
                    else (fraction - delta).fastCoerceIn(0f, 1f)
            }
        )
    }

    // Handles Dragging (Instantly sticks thumb to finger)
    LaunchedEffect(dampedDragAnimation) {
        snapshotFlow { fraction }
            .collectLatest { frac ->
                // ONLY snap the value instantly if the user is physically dragging it
                if (isInteracting) {
                    dampedDragAnimation.updateValue(frac)
                }
            }
    }

    // Handles External Taps / State Changes (Smoothly animates)
    LaunchedEffect(selected) {
        snapshotFlow { selected() }
            .collectLatest { isSelected ->
                val target = if (isSelected) 1f else 0f

                // If state changes externally (Card tap) or via Switch tap, gracefully glide to it.
                if (target != dampedDragAnimation.value) {
                    fraction = target
                    dampedDragAnimation.animateToValue(target)
                }
            }
    }

    val trackBackdrop = rememberLayerBackdrop()

    Box(
        modifier = modifier,
        contentAlignment = Alignment.CenterStart
    ) {
        Box(
            Modifier
                .layerBackdrop(trackBackdrop)
                .clip(ContinuousCapsule)
                .drawBehind {
                    val currentFraction = dampedDragAnimation.value
                    drawRect(lerp(trackColor, switchColor, currentFraction))
                }
                .size(trackWidth, trackHeight)
        )

        Box(
            Modifier
                .graphicsLayer {
                    val currentFraction = dampedDragAnimation.value
                    translationX =
                        if (isLtr) lerp(paddingPx, paddingPx + dragWidthPx, currentFraction)
                        else lerp(-paddingPx, -(paddingPx + dragWidthPx), currentFraction)
                }
                .semantics { role = Role.Switch }
                .then(dampedDragAnimation.modifier)
                .drawBackdrop(
                    backdrop = rememberCombinedBackdrop(
                        backdrop,
                        rememberBackdrop(trackBackdrop) { drawBackdrop ->
                            val progress = dampedDragAnimation.pressProgress
                            val scaleX = lerp(2f / 3f, 0.75f, progress)
                            val scaleY = lerp(0f, 0.75f, progress)
                            scale(scaleX, scaleY) { drawBackdrop() }
                        }
                    ),
                    shape = { ContinuousCapsule },
                    effects = {
                        val progress = dampedDragAnimation.pressProgress
                        blur(blurRadiusPx * (1f - progress))
                        lens(
                            lensDistPx * progress,
                            lensScalePx * progress,
                            chromaticAberration = true
                        )
                    },
                    highlight = {
                        val progress = dampedDragAnimation.pressProgress
                        Highlight.Ambient.copy(
                            width = Highlight.Ambient.width / 1.5f,
                            blurRadius = Highlight.Ambient.blurRadius / 1.5f,
                            alpha = progress
                        )
                    },
                    shadow = {
                        Shadow(radius = shadowRadiusDp, color = Color.Black.copy(alpha = 0.05f))
                    },
                    innerShadow = {
                        val progress = dampedDragAnimation.pressProgress
                        InnerShadow(radius = shadowRadiusDp * progress, alpha = progress)
                    },
                    layerBlock = {
                        scaleX = dampedDragAnimation.scaleX
                        scaleY = dampedDragAnimation.scaleY
                        val velocity = dampedDragAnimation.velocity / 50f
                        scaleX /= 1f - (velocity * 0.75f).fastCoerceIn(-0.2f, 0.2f)
                        scaleY *= 1f - (velocity * 0.25f).fastCoerceIn(-0.2f, 0.2f)
                    },
                    onDrawSurface = {
                        val progress = dampedDragAnimation.pressProgress
                        drawRect(thumbColor.copy(alpha = 1f - progress))
                    }
                )
                .size(thumbWidth, thumbHeight)
        )
    }
}