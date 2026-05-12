package com.wvelabs.core_ui.components.liquidtabbar

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.EaseOut
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastCoerceIn
import androidx.compose.ui.util.fastRoundToInt
import androidx.compose.ui.util.lerp
import com.kyant.backdrop.Backdrop
import com.kyant.backdrop.BackdropEffectScope
import com.kyant.backdrop.backdrops.layerBackdrop
import com.kyant.backdrop.backdrops.rememberCombinedBackdrop
import com.kyant.backdrop.backdrops.rememberLayerBackdrop
import com.kyant.backdrop.drawBackdrop
import com.kyant.backdrop.highlight.Highlight
import com.kyant.backdrop.shadow.InnerShadow
import com.kyant.backdrop.shadow.Shadow
import com.kyant.capsule.ContinuousCapsule
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.sign

@Composable
fun LiquidBottomTabs(
    modifier: Modifier = Modifier,
    selectedTabIndex: () -> Int,
    onTabSelected: (index: Int) -> Unit,
    backdrop: Backdrop,
    tabsCount: Int,
    containerHeight: Dp = 64.dp,
    containerShape: Shape = ContinuousCapsule,
    containerColor: Color = Color.White.copy(alpha = 0.4f),
    selectedTabColor: Color = Color.Black,
    unselectedTabColor: Color = Color.DarkGray,
    containerEffects: BackdropEffectScope.() -> Unit = {},
    tabsEffects: BackdropEffectScope.(progress: Float) -> Unit = {},
    indicatorEffects: BackdropEffectScope.(progress: Float) -> Unit = {},
    padding: Dp = 0.dp,
    borderStroke: BorderStroke = BorderStroke(
        1.dp, Brush.verticalGradient(listOf(Color.White, Color.Black))
    ),
    tabItem: @Composable RowScope.(index: Int, measurementModifier: Modifier, providedColor: Color, isBaseLayer: Boolean) -> Unit
) {
    val tabsBackdrop = rememberLayerBackdrop()
    val tabHeight = containerHeight - (padding * 2)

    val tabWidths = remember(tabsCount) { mutableStateListOf(*Array(tabsCount) { 0f }) }
    val tabPositions = remember(tabsCount) { mutableStateListOf(*Array(tabsCount) { 0f }) }

    BoxWithConstraints(
        modifier = modifier, contentAlignment = Alignment.CenterStart
    ) {
        val density = LocalDensity.current
        val indication = LocalIndication.current
        val interactionSources =
            remember(tabsCount) { List(tabsCount) { MutableInteractionSource() } }

        val actualIndex = selectedTabIndex()

        val offsetAnimation = remember { Animatable(0f) }
        val panelOffset by remember(density) {
            derivedStateOf {
                val fraction = (offsetAnimation.value / constraints.maxWidth).fastCoerceIn(-1f, 1f)
                with(density) { padding.toPx() * fraction.sign * EaseOut.transform(abs(fraction)) }
            }
        }

        val animationScope = rememberCoroutineScope()
        var lastHandledIndex by remember { mutableIntStateOf(actualIndex) }
        val dampedDragAnimation = remember(animationScope) {
            DampedDragAnimation(
                animationScope = animationScope,
                initialValue = actualIndex.toFloat(),
                valueRange = 0f..(tabsCount - 1).toFloat(),
                visibilityThreshold = 0.001f,
                initialScale = 1f,
                pressedScale = 72f / 42f,
                onDragStarted = {},
                onDragStopped = {
                    val targetIndex = targetValue.fastRoundToInt().fastCoerceIn(0, tabsCount - 1)
                    onTabSelected(targetIndex) // 🟢 Trigger navigation if dragging finishes
                    animateToValue(targetIndex.toFloat())
                    animationScope.launch { offsetAnimation.animateTo(0f, spring(1f, 300f, 0.5f)) }
                },
                onDrag = { _, dragAmount ->
                    val avgSpacing =
                        if (tabsCount > 1) (tabPositions.last() - tabPositions.first()) / (tabsCount - 1) else 100f
                    val effectiveSpacing = if (avgSpacing > 0f) avgSpacing else 100f

                    updateValue(
                        (targetValue + dragAmount.x / effectiveSpacing).fastCoerceIn(
                            0f, (tabsCount - 1).toFloat()
                        )
                    )
                    animationScope.launch { offsetAnimation.snapTo(offsetAnimation.value + dragAmount.x) }
                })
        }

        // 🟢 Auto-Sync with Back Button / External Navigation
        LaunchedEffect(actualIndex) {
            if (actualIndex != lastHandledIndex) {
                dampedDragAnimation.updateValue(actualIndex.toFloat())
                lastHandledIndex = actualIndex
            }
        }

        val syntheticPress = remember { Animatable(0f) }
        val activePressProgress by remember(dampedDragAnimation) {
            derivedStateOf { max(dampedDragAnimation.pressProgress, syntheticPress.value) }
        }

        val createMeasurementModifier: (Int) -> Modifier = { index ->
            Modifier
                .onGloballyPositioned { coordinates ->
                    val width = coordinates.size.width.toFloat()
                    val x = coordinates.positionInParent().x
                    if (tabWidths[index] != width) tabWidths[index] = width
                    if (tabPositions[index] != x) tabPositions[index] = x
                }
                .clip(CircleShape)
                .clickable(
                    interactionSource = interactionSources[index],
                    indication = indication,
                    onClick = {
                        if (actualIndex != index) {
                            lastHandledIndex = index

                            animationScope.launch {
                                // Notify the ViewModel of the tab change
                                onTabSelected(index)

                                // Smoothly glide the indicator to the new tab!
                                dampedDragAnimation.animateToValueSlow(index.toFloat())
                            }
                        }
                    }
                )
        }

        val interactiveHighlight = remember(animationScope, tabPositions, tabWidths) {
            InteractiveHighlight(
                animationScope = animationScope, position = { size, offset ->
                    val progress = dampedDragAnimation.value.coerceIn(0f, (tabsCount - 1).toFloat())
                    val startIndex = progress.toInt()
                    val endIndex = (startIndex + 1).coerceAtMost(tabsCount - 1)
                    val fraction = progress - startIndex

                    val currentX = lerp(tabPositions[startIndex], tabPositions[endIndex], fraction)
                    val currentWidth = lerp(tabWidths[startIndex], tabWidths[endIndex], fraction)

                    Offset(
                        x = currentX + panelOffset + (currentWidth / 2f),
                        y = size.height / 2f
                    )
                })
        }

        // 1. Visible Glass Container
        Row(
            Modifier
                .graphicsLayer { translationX = panelOffset }
                .drawBackdrop(
                    backdrop = backdrop,
                    shape = { containerShape },
                    effects = containerEffects,
                    layerBlock = {
                        val progress = activePressProgress
                        val scale = lerp(1f, 1f + 16f.dp.toPx() / size.width, progress)
                        scaleX = scale
                        scaleY = scale
                    },
                    onDrawSurface = { drawRect(containerColor) })
                .then(interactiveHighlight.modifier)
                .height(containerHeight)
                .fillMaxWidth()
                .border(borderStroke, containerShape)
                .padding(padding),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            for (i in 0 until tabsCount) {
                tabItem(i, createMeasurementModifier(i), unselectedTabColor, true)
            }
        }

        CompositionLocalProvider(
            LocalLiquidBottomTabScale provides {
                lerp(1f, 1.2f, activePressProgress)
            }) {
            // Invisible Backdrop Catcher (Used for Lens effect)
            Row(
                Modifier
                    .clearAndSetSemantics {}
                    .alpha(0f)
                    .layerBackdrop(tabsBackdrop)
                    .graphicsLayer { translationX = panelOffset }
                    .drawBackdrop(
                        backdrop = backdrop,
                        shape = { containerShape },
                        effects = { tabsEffects(activePressProgress) },
                        highlight = { Highlight.Default.copy(alpha = activePressProgress) },
                        onDrawSurface = { drawRect(selectedTabColor, alpha = 0.1f) })
                    .then(interactiveHighlight.modifier)
                    .height(tabHeight + (padding / 2))
                    .fillMaxWidth()
                    .padding(padding),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                for (i in 0 until tabsCount) {
                    tabItem(i, createMeasurementModifier(i), selectedTabColor, false)
                }
            }
        }

        // The Sliding Liquid Glass Indicator (Lens)
        val progress = dampedDragAnimation.value.coerceIn(0f, (tabsCount - 1).toFloat())
        val startIndex = progress.toInt()
        val endIndex = (startIndex + 1).coerceAtMost(tabsCount - 1)
        val fraction = progress - startIndex

        val isMeasured = tabWidths[startIndex] != 0f && tabWidths[endIndex] != 0f
        if (isMeasured) {
            val currentXPx = lerp(tabPositions[startIndex], tabPositions[endIndex], fraction)
            val currentWidthPx = lerp(tabWidths[startIndex], tabWidths[endIndex], fraction)
            Box(
                Modifier
                    .graphicsLayer {
                        translationX = currentXPx + panelOffset
                    }
                    .then(interactiveHighlight.gestureModifier)
                    .then(dampedDragAnimation.modifier)
                    .drawBackdrop(
                        backdrop = rememberCombinedBackdrop(backdrop, tabsBackdrop),
                        shape = { containerShape },
                        effects = { indicatorEffects(activePressProgress) },
                        highlight = { Highlight.Default.copy(alpha = activePressProgress) },
                        shadow = { Shadow(alpha = activePressProgress) },
                        innerShadow = {
                            val pressProg = activePressProgress
                            InnerShadow(radius = 8f.dp * pressProg, alpha = pressProg)
                        },
                        layerBlock = {
                            shape = containerShape
                            alpha = activePressProgress
                            scaleX = dampedDragAnimation.scaleX
                            scaleY = dampedDragAnimation.scaleY
                            val velocity = dampedDragAnimation.velocity / 15f
                            scaleX /= 1f - (velocity * 0.75f).fastCoerceIn(-0.2f, 0.2f)
                            scaleY *= 1f - (velocity * 0.25f).fastCoerceIn(-0.2f, 0.2f)
                        },
                        onDrawSurface = {
                            val pressProg = activePressProgress
                            drawRoundRect(
                                color = containerColor.copy(alpha = 1f),
                                alpha = 0.05f * (1f - pressProg),
                                cornerRadius = CornerRadius(size.height / 2f)
                            )
                        })
                    .height(tabHeight)
                    .width(with(density) { currentWidthPx.toDp() })
            )
        }
    }
}