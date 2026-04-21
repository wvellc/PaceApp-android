package com.example.paceapp.core.components

import androidx.compose.animation.core.Animatable
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.example.paceapp.core.extensions.defaultClickable
import com.example.paceapp.theme.AppColors
import com.example.paceapp.theme.AppTheme
import com.kyant.capsule.ContinuousRoundedRectangle
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@Composable
fun <T> AppSegmentedButtons(
    segments: List<T>,
    selectedSegment: T,
    onSegmentSelected: (T) -> Unit,
    modifier: Modifier = Modifier,
    itemTitle: @Composable (T) -> String = { it.toString() }, // 3. Generic Title Extractor
    buttonHeight: Dp = 36.dp,
    segmentShape: Shape = ContinuousRoundedRectangle(8.dp)
) {
    // Safely find the index of the selected item
    val selectedIndex = segments.indexOf(selectedSegment).coerceAtLeast(0)

    var segmentWidth by remember { mutableFloatStateOf(0f) }
    val offsetX = remember { Animatable(0f) }
    val coroutineScope = rememberCoroutineScope()

    // Animate to the new index whenever the hoisted state changes
    LaunchedEffect(selectedIndex, segmentWidth) {
        if (segmentWidth > 0f) {
            offsetX.animateTo(selectedIndex * segmentWidth)
        }
    }

    Box(
        modifier = modifier

            .onSizeChanged {
                segmentWidth = it.width / segments.size.toFloat()
            }
            .pointerInput(Unit) {
                detectDragGestures(
                    onDrag = { change, dragAmount ->
                        change.consume()
                        val newOffset = (offsetX.value + dragAmount.x)
                            .coerceIn(0f, segmentWidth * (segments.size - 1))
                        coroutineScope.launch { offsetX.snapTo(newOffset) }
                    },
                    onDragCancel = {
                        // If the system cancels the drag, snap back to the currently selected item
                        coroutineScope.launch {
                            offsetX.animateTo(selectedIndex * segmentWidth)
                        }
                    },
                    onDragEnd = {
                        val nearestIndex = (offsetX.value / segmentWidth)
                            .roundToInt()
                            .coerceIn(0, segments.lastIndex)

                        // Tell the parent the new selection
                        onSegmentSelected(segments[nearestIndex])

                        // Force the thumb to animate to the exact boundary of the nearest index!
                        coroutineScope.launch {
                            offsetX.animateTo(nearestIndex * segmentWidth)
                        }
                    }
                )
            }
    ) {
        // Sliding thumb
        Box(
            modifier = Modifier
                .offset { IntOffset(offsetX.value.roundToInt(), 0) }
                .width(with(LocalDensity.current) { segmentWidth.toDp() })
                .height(buttonHeight)
                .background(
                    brush = Brush.verticalGradient(AppColors.segmentButtonGradient),
                    shape = segmentShape
                )
        )

        Row(modifier = Modifier.fillMaxWidth()) {
            segments.forEach { segment ->
                SegmentItem(
                    title = itemTitle(segment),
                    modifier = Modifier
                        .height(buttonHeight)
                        .weight(1f)
                        .clip(segmentShape),
                    onClick = { onSegmentSelected(segment) }
                )
            }
        }
    }
}

@Composable
private fun SegmentItem(
    title: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .defaultClickable(onClick = onClick) // Assuming your custom debounce modifier
            .padding(vertical = 8.dp),
    ) {
        Text(
            text = title,
            style = AppTheme.typography.size14.copy(
                fontWeight = FontWeight.Medium,
            ),
            color = AppColors.White
        )
    }
}

@Composable
@Preview
fun SegmentedButtonsPreview() = AppSegmentedButtons(
    listOf("First", "Second", "Third"),
    selectedSegment = "First",
    onSegmentSelected = {},
    Modifier,
)

@Composable
@Preview
fun SegmentItemPreview() = SegmentItem(title = "TITLE")