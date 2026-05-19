package com.wvelabs.core_ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

enum class SwipeDirection {
    StartToEnd,
    EndToStart,
    Both
}

// The State class that holds the data
class SwipeActionState {
    var openItemIds by mutableStateOf<Set<String>>(emptySet())
        private set

    /**
     * Marks an item as actively swiping.
     * @param id The unique identifier of the item.
     * @param closeOthers If true, clears all other open items. If false, adds this to the open set.
     */
    fun openItem(id: String, closeOthers: Boolean = true) {
        openItemIds = if (closeOthers) {
            setOf(id) // Erase everything else, only keep this one
        } else {
            openItemIds + id // Append this item to the set of open items
        }
    }

    /**
     * Removes an item from the open set (used when manually swiped shut).
     */
    fun closeItem(id: String) {
        openItemIds = openItemIds - id
    }

    /**
     * Resets the state, causing ALL open items to animate closed.
     */
    fun closeAll() {
        openItemIds = emptySet()
    }
}

// The standard Compose remember function
@Composable
fun rememberSwipeActionState(): SwipeActionState {
    return remember { SwipeActionState() }
}

@Composable
fun SwipeToActionBox(
    modifier: Modifier = Modifier,
    itemId: String,
    state: SwipeActionState = rememberSwipeActionState(),
    swipeThreshold: Dp = 100.dp,
    direction: SwipeDirection = SwipeDirection.EndToStart,
    actionBackgroundColor: Color = Color.Transparent,
    actionShape: Shape = RectangleShape,
    closeOtherItemsOnSwipe: Boolean = true,
    actions: @Composable (RowScope.() -> Unit)? = null,
    content: @Composable BoxScope.() -> Unit,
) {
    val coroutineScope = rememberCoroutineScope()
    val offsetX = remember { Animatable(0f) }
    val swipeThresholdPx = with(LocalDensity.current) { swipeThreshold.toPx() }

    val minBound =
        if (direction == SwipeDirection.EndToStart || direction == SwipeDirection.Both) -swipeThresholdPx else 0f
    val maxBound =
        if (direction == SwipeDirection.StartToEnd || direction == SwipeDirection.Both) swipeThresholdPx else 0f
    // Keep latest callback reference for pointerInput

    // ---  The Auto-Close Trigger ---
    LaunchedEffect(state.openItemIds) {
        if (!state.openItemIds.contains(itemId) && offsetX.value != 0f) {
            offsetX.animateTo(0f, tween(300))
        }
    }
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        // --- THE BACKGROUND LAYER ---
        if (actions != null) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    // Apply clipping and color ONLY to the background layer!
                    .clip(actionShape)
                    .background(actionBackgroundColor)
            ) {
                // Automatically determine which side to pin the actions
                val actionAlignment = when (direction) {
                    SwipeDirection.StartToEnd -> Alignment.CenterStart
                    SwipeDirection.EndToStart -> Alignment.CenterEnd
                    SwipeDirection.Both -> if (offsetX.value < 0f) Alignment.CenterEnd else Alignment.CenterStart
                }

                // A Row container exactly the width of the swipe threshold
                Row(
                    modifier = Modifier
                        .align(actionAlignment)
                        .width(swipeThreshold)
                        .fillMaxHeight(),
                    horizontalArrangement = Arrangement.SpaceEvenly, // Distributes multiple icons nicely
                    verticalAlignment = Alignment.CenterVertically,
                    content = actions
                )
            }
        }

        // --- THE FOREGROUND LAYER ---
        Box(
            modifier = Modifier
                .offset { IntOffset(offsetX.value.roundToInt(), 0) }
                .pointerInput(direction, swipeThresholdPx) {
                    detectHorizontalDragGestures(
                        onDragStart = {
                            state.openItem(itemId, closeOthers = closeOtherItemsOnSwipe)
                        },
                        onHorizontalDrag = { change, dragAmount ->
                            change.consume()
                            val target = (offsetX.value + dragAmount).coerceIn(minBound, maxBound)
                            coroutineScope.launch { offsetX.snapTo(target) }
                        },
                        onDragEnd = {
                            coroutineScope.launch {
                                val targetValue = when {
                                    offsetX.value <= -swipeThresholdPx / 2 -> -swipeThresholdPx
                                    offsetX.value >= swipeThresholdPx / 2 -> swipeThresholdPx
                                    else -> 0f
                                }
                                offsetX.animateTo(
                                    targetValue = targetValue,
                                    animationSpec = tween(durationMillis = 300)
                                )
                            }
                        }
                    )
                },
            content = content
        )
    }
}