package com.example.paceapp.core.components

import GenericWheelPicker
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.paceapp.theme.AppColors
import com.example.paceapp.theme.AppTheme
import kotlin.math.abs
import kotlin.math.roundToInt

@Composable
fun <T> AppDigitPicker(
    modifier: Modifier = Modifier,
    initialValue: T,
    onValueChange: (Double) -> Unit,
    range: ClosedRange<T>,
    step: T,
    visibleItems: Int = 3,
    selectedItemBackground: @Composable () -> Unit = { DefaultBackground() },
    itemSpacing: Dp = 8.dp,
    itemHeight: Dp = 28.dp,
    padWithZero: Boolean = false,
    selectedTextStyle: TextStyle = AppTheme.typography.medium.copy(
        fontSize = 22.sp,
        color = AppColors.White
    ),
    unselectedTextStyle: TextStyle = AppTheme.typography.regular.copy(
        fontSize = 18.sp,
        color = AppColors.White.copy(alpha = 0.35f)
    )
) where T : Number, T : Comparable<T> {

    val valDouble = initialValue.toDouble()
    val stepDouble = step.toDouble()
    val startDouble = range.start.toDouble()
    val endDouble = range.endInclusive.toDouble()
    val isDecimal = stepDouble % 1.0 != 0.0

    val wholeNumbers = remember(range) {
        (startDouble.toInt()..endDouble.toInt()).toList()
    }
    val decimals = remember { (0..9).toList() }

    // --- Calculate incoming expected parts ---
    val incomingWhole = valDouble.toInt()
    val incomingDecimal = ((valDouble - incomingWhole) * 10).roundToInt().coerceIn(0, 9)

    // --- Internal State ---
    var currentWhole by remember { mutableIntStateOf(incomingWhole) }
    var currentDecimal by remember { mutableIntStateOf(incomingDecimal) }


    var recomposeKey by remember { mutableIntStateOf(0) }

    // Watch for incoming changes from the ViewModel (like when DB finishes loading)
    LaunchedEffect(initialValue) {
        val calculatedCurrent = currentWhole.toDouble() + (currentDecimal / 10.0)

        // If the ViewModel sends down a value that is DIFFERENT from what the wheel is
        // currently pointing at, it means the database just loaded!
        if (abs(valDouble - calculatedCurrent) > 0.01) {
            currentWhole = incomingWhole
            currentDecimal = incomingDecimal
            // Incrementing the key forces the GenericWheelPicker to jump to the new values!
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
            // --- Whole Number Wheel ---
            GenericWheelPicker(
                items = wholeNumbers,
                itemHeight = itemHeight,
                visibleItemsCount = visibleItems,
                initialIndex = wholeNumbers.indexOf(currentWhole).coerceAtLeast(0),
                itemLabel = { if (padWithZero) it.toString().padStart(2, '0') else it.toString() },
                selectorBackground = selectedItemBackground,
                selectedStyle = selectedTextStyle,
                spacing = itemSpacing,
                unselectedStyle = unselectedTextStyle,
                onItemSelected = { newWhole ->
                    currentWhole = newWhole
                    // Tell the ViewModel the user scrolled
                    onValueChange(currentWhole.toDouble() + (currentDecimal / 10.0))
                },
                modifier = Modifier.weight(1f)
            )

            if (isDecimal) {
                // --- Separator ---
                Box(
                    modifier = Modifier
                        .padding(horizontal = 8.dp)
                        .size(6.dp)
                        .background(Color.White, CircleShape)
                )

                // --- Decimal Wheel ---
                GenericWheelPicker(
                    items = decimals,
                    itemHeight = itemHeight,
                    visibleItemsCount = visibleItems,
                    initialIndex = currentDecimal,
                    itemLabel = { it.toString() },
                    spacing = itemSpacing,
                    selectorBackground = selectedItemBackground,
                    selectedStyle = selectedTextStyle,
                    unselectedStyle = unselectedTextStyle,
                    onItemSelected = { newDecimal ->
                        currentDecimal = newDecimal
                        // Tell the ViewModel the user scrolled
                        onValueChange(currentWhole.toDouble() + (currentDecimal / 10.0))
                    },
                    modifier = Modifier.weight(1f)
                )
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