package net.paceapp.core.components

import GenericWheelPicker
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import net.paceapp.theme.AppColors
import net.paceapp.theme.AppTheme

@Composable
fun AppDurationPicker(
    modifier: Modifier = Modifier,
    initialDurationSeconds: Long,
    onDurationChange: (Long) -> Unit,
    maxHours: Int = 23,
    visibleItems: Int = 3,
    selectedItemBackground: @Composable () -> Unit = { DefaultBackground() },
    itemSpacing: Dp = 8.dp,
    itemHeight: Dp = 28.dp,
    selectedFontSize: TextUnit = 22.sp,
    fontSize: TextUnit = 18.sp,
    contentColor: Color = Color.White,
    trailingIconSpacing: Dp = 16.dp, // Spacing between the picker and the trailing icon
    trailingIcon: @Composable (RowScope.() -> Unit)? = null
) {
    val selectedTextStyle: TextStyle = AppTheme.typography.medium.copy(
        fontSize = selectedFontSize,
        color = contentColor
    )
    val unselectedTextStyle: TextStyle = AppTheme.typography.regular.copy(
        fontSize = fontSize,
        color = contentColor.copy(alpha = 0.35f)
    )

    // Lists for wheels
    val hoursList = remember(maxHours) { (0..maxHours).toList() }
    val minutesList = remember { (0..59).toList() }
    val secondsList = remember { (0..59).toList() }

    // --- Calculate incoming expected parts ---
    val incomingHours = (initialDurationSeconds / 3600).toInt()
    val incomingMinutes = ((initialDurationSeconds % 3600) / 60).toInt()
    val incomingSeconds = (initialDurationSeconds % 60).toInt()

    // --- Internal State ---
    var currentHours by remember { mutableIntStateOf(incomingHours) }
    var currentMinutes by remember { mutableIntStateOf(incomingMinutes) }
    var currentSeconds by remember { mutableIntStateOf(incomingSeconds) }

    var recomposeKey by remember { mutableIntStateOf(0) }

    // Watch for incoming changes from the ViewModel
    LaunchedEffect(initialDurationSeconds) {
        val calculatedCurrent = (currentHours * 3600L) + (currentMinutes * 60L) + currentSeconds

        // If the ViewModel sends a value that is DIFFERENT from the wheel's current state
        if (initialDurationSeconds != calculatedCurrent) {
            currentHours = incomingHours
            currentMinutes = incomingMinutes
            currentSeconds = incomingSeconds
            recomposeKey++
        }
    }

    // Wrap the wheels in the key so they recreate ONLY when recomposeKey changes
    key(recomposeKey) {
        Row(
            modifier = modifier,
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            // --- Hours Wheel ---
            GenericWheelPicker(
                items = hoursList,
                itemHeight = itemHeight,
                visibleItemsCount = visibleItems,
                initialIndex = hoursList.indexOf(currentHours).coerceAtLeast(0),
                itemLabel = { it.toString().padStart(2, '0') }, // 00, 01, etc.
                selectorBackground = selectedItemBackground,
                selectedStyle = selectedTextStyle,
                spacing = itemSpacing,
                unselectedStyle = unselectedTextStyle,
                onItemSelected = { newHours ->
                    currentHours = newHours
                    onDurationChange((currentHours * 3600L) + (currentMinutes * 60L) + currentSeconds)
                },
                modifier = Modifier.weight(1f)
            )

            // --- Separator 1 (:) ---
            Text(
                text = ":",
                style = selectedTextStyle,
                modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
            )

            // --- Minutes Wheel ---
            GenericWheelPicker(
                items = minutesList,
                itemHeight = itemHeight,
                visibleItemsCount = visibleItems,
                initialIndex = currentMinutes,
                itemLabel = { it.toString().padStart(2, '0') },
                selectorBackground = selectedItemBackground,
                selectedStyle = selectedTextStyle,
                spacing = itemSpacing,
                unselectedStyle = unselectedTextStyle,
                onItemSelected = { newMinutes ->
                    currentMinutes = newMinutes
                    onDurationChange((currentHours * 3600L) + (currentMinutes * 60L) + currentSeconds)
                },
                modifier = Modifier.weight(1f)
            )

            // --- Separator 2 (:) ---
            Text(
                text = ":",
                style = selectedTextStyle,
                modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
            )

            // --- Seconds Wheel ---
            GenericWheelPicker(
                items = secondsList,
                itemHeight = itemHeight,
                visibleItemsCount = visibleItems,
                initialIndex = currentSeconds,
                itemLabel = { it.toString().padStart(2, '0') },
                selectorBackground = selectedItemBackground,
                selectedStyle = selectedTextStyle,
                spacing = itemSpacing,
                unselectedStyle = unselectedTextStyle,
                onItemSelected = { newSeconds ->
                    currentSeconds = newSeconds
                    onDurationChange((currentHours * 3600L) + (currentMinutes * 60L) + currentSeconds)
                },
                modifier = Modifier.weight(1f)
            )

            // --- Trailing Icon with Spacing ---
            if (trailingIcon != null) {
                Spacer(modifier = Modifier.width(trailingIconSpacing))
                trailingIcon()
            }
        }
    }
}

@Composable
private fun DefaultBackground() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(32.dp)
            .clip(RoundedCornerShape(50))
            .background(AppColors.White.copy(alpha = 0.08f))
    )
}