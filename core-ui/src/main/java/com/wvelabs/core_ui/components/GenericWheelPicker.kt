package com.wvelabs.core_ui.components

import android.media.AudioAttributes
import android.media.SoundPool
import androidx.annotation.RawRes
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
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
import androidx.compose.runtime.remember
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
    visibleItemsCount: Int = 5, // 5 provides better context for dynamic scaling
    itemHeight: Dp = 48.dp,
    unselectedStyle: TextStyle = TextStyle(
        fontSize = 16.sp,
        color = Color.Gray,
        fontWeight = FontWeight.Normal
    ),
    selectedStyle: TextStyle = TextStyle(
        fontSize = 22.sp,
        color = Color(0xFF007AFF),
        fontWeight = FontWeight.Bold
    ),
    selectionBackgroundColor: Color = Color(0xFF007AFF).copy(alpha = 0.1f),
    selectionBackgroundCornerRadius: Dp = 8.dp,

) {
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
    val tickSoundId = remember { soundPool.load(context, R.raw.wheel_tick, 1) }

    val currentCenteredIndex = remember {
        derivedStateOf {
            val layoutInfo = listState.layoutInfo
            val visibleItems = layoutInfo.visibleItemsInfo
            if (visibleItems.isEmpty()) return@derivedStateOf initialIndex

            val centerOffset = layoutInfo.viewportEndOffset / 2
            visibleItems.minByOrNull {
                kotlin.math.abs(it.offset + (it.size / 2) - centerOffset)
            }?.index ?: 0
        }
    }

    LaunchedEffect(currentCenteredIndex.value) {
        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
        soundPool.play(tickSoundId, 0.3f, 0.3f, 1, 0, 1.0f)
        onItemSelected(items[currentCenteredIndex.value])
    }

    DisposableEffect(Unit) {
        onDispose { soundPool.release() }
    }

    Box(
        modifier = modifier
            .height(itemHeight * visibleItemsCount)
            .fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        // --- STATIC BACKGROUND ---
        // This box does not move. It acts as the "selector" window.
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(itemHeight)
                .padding(horizontal = 8.dp)
                .background(
                    color = selectionBackgroundColor,
                    shape = RoundedCornerShape(selectionBackgroundCornerRadius)
                )
        )

        // --- SCROLLING WHEEL ---
        LazyColumn(
            state = listState,
            flingBehavior = snapFlingBehavior,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(vertical = itemHeight * (visibleItemsCount / 2))
        ) {
            itemsIndexed(items) { index, item ->
                val isSelected = index == currentCenteredIndex.value

                Box(
                    modifier = Modifier
                        .height(itemHeight)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = item.toString(),
                        // Dynamic styling based on center position
                        style = if (isSelected) selectedStyle else unselectedStyle
                    )
                }
            }
        }
    }
}