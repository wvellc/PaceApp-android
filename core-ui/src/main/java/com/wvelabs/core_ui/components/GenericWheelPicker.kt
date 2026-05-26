import android.media.AudioAttributes
import android.media.SoundPool
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wvelabs.core_ui.R

@Composable
fun <T> GenericWheelPicker(
    items: List<T>,
    initialIndex: Int,
    onItemSelected: (T) -> Unit,
    modifier: Modifier = Modifier,
    visibleItemsCount: Int = 5,
    itemHeight: Dp = 48.dp,
    spacing: Dp = 0.dp,
    itemLabel: (T) -> String = { it.toString() },
    unselectedStyle: TextStyle = TextStyle(
        fontSize = 16.sp,
        color = Color.Gray,
        fontWeight = FontWeight.Normal
    ),
    selectedStyle: TextStyle = TextStyle(
        fontSize = 22.sp,
        color = Color.White,
        fontWeight = FontWeight.Bold
    ),
    selectorBackground: @Composable () -> Unit = {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(itemHeight)
                .padding(horizontal = 8.dp)
                .background(
                    color = Color(0xFF007AFF).copy(alpha = 0.1f),
                    shape = RoundedCornerShape(8.dp)
                )
        )
    }
) {
    val safeVisibleItems = visibleItemsCount
        .coerceIn(3, 9)
        .let { if (it % 2 == 0) it - 1 else it }

    val context = LocalContext.current
    val listState = rememberLazyListState(initialFirstVisibleItemIndex = initialIndex)
    val snapFlingBehavior = rememberSnapFlingBehavior(lazyListState = listState)
    val haptic = LocalHapticFeedback.current

    // Audio Setup
    val soundPool = remember {
        val attributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()

        SoundPool.Builder().setMaxStreams(1).setAudioAttributes(attributes).build()
    }
    val tickSoundId = remember { soundPool.load(context, R.raw.wheel_tick_soft, 1) }

    var hasUserInteracted by remember { mutableStateOf(false) }

    LaunchedEffect(listState.isScrollInProgress) {
        if (listState.isScrollInProgress) {
            hasUserInteracted = true
        }
    }

    val currentCenteredIndex by remember {
        derivedStateOf {
            val layoutInfo = listState.layoutInfo
            val visibleItemsInfo = layoutInfo.visibleItemsInfo
            if (visibleItemsInfo.isEmpty()) return@derivedStateOf initialIndex

            val centerOffset =
                layoutInfo.viewportStartOffset + (layoutInfo.viewportEndOffset - layoutInfo.viewportStartOffset) / 2

            var closestIndex = initialIndex
            var minDistance = Int.MAX_VALUE

            for (itemInfo in visibleItemsInfo) {
                val itemCenter = itemInfo.offset + (itemInfo.size / 2)
                val distance = kotlin.math.abs(itemCenter - centerOffset)
                if (distance < minDistance) {
                    minDistance = distance
                    closestIndex = itemInfo.index
                }
            }
            closestIndex
        }
    }

    LaunchedEffect(currentCenteredIndex) {
        if (hasUserInteracted) {
            haptic.performHapticFeedback(HapticFeedbackType.GestureEnd)
            soundPool.play(tickSoundId, 0.5f, 0.5f, 1, 0, 1.0f)
            onItemSelected(items[currentCenteredIndex])
        }
    }

    DisposableEffect(Unit) {
        onDispose { soundPool.release() }
    }

    // --- NEW: Bulletproof Layout Math ---
    val containerHeight = (itemHeight * safeVisibleItems) + (spacing * (safeVisibleItems - 1))
    val halfVisible = safeVisibleItems / 2
    val verticalPadding = (itemHeight * halfVisible) + (spacing * halfVisible)

    Box(
        modifier = modifier
            .height(containerHeight) // Uses precise mathematical height
            .fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        selectorBackground()

        LazyColumn(
            state = listState,
            flingBehavior = snapFlingBehavior,
            verticalArrangement = Arrangement.spacedBy(spacing), // Applies the gap!
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(vertical = verticalPadding) // Perfectly centers item 0
        ) {
            itemsIndexed(items) { index, item ->
                val isSelected = index == currentCenteredIndex

                Box(
                    modifier = Modifier
                        .height(itemHeight)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = itemLabel(item),
                        style = if (isSelected) selectedStyle else unselectedStyle
                    )
                }
            }
        }
    }
}