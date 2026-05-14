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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.paceapp.theme.AppColors
import com.example.paceapp.theme.AppTheme
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
    //Convert values to double
    val valDouble = initialValue.toDouble()
    val stepDouble = step.toDouble()
    val startDouble = range.start.toDouble()
    val endDouble = range.endInclusive.toDouble()
    val isDecimal = stepDouble % 1.0 != 0.0//Check step for decimals
    //Number list of whole numbers
    val wholeNumbers = remember(range) {
        (startDouble.toInt()..endDouble.toInt()).toList()
    }
    //Decimal number list
    val decimals = remember { (0..9).toList() }

    // --- Track internal state based on initial value ---
    val initialWholePart = valDouble.toInt()
    val initialDecimalPart = ((valDouble - initialWholePart) * 10).roundToInt().coerceIn(0, 9)

    var currentWhole by remember { mutableIntStateOf(initialWholePart) }
    var currentDecimal by remember { mutableIntStateOf(initialDecimalPart) }


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
            initialIndex = wholeNumbers.indexOf(initialWholePart).coerceAtLeast(0),
            itemLabel = { if (padWithZero) it.toString().padStart(2, '0') else it.toString() },
            selectorBackground = selectedItemBackground,
            selectedStyle = selectedTextStyle,
            spacing = itemSpacing,
            unselectedStyle = unselectedTextStyle,
            onItemSelected = { newWhole ->
                // Use the internal state to calculate the full double
                currentWhole = newWhole
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
                initialIndex = initialDecimalPart,
                itemLabel = { it.toString() },
                spacing = itemSpacing,
                selectorBackground = selectedItemBackground,
                selectedStyle = selectedTextStyle,
                unselectedStyle = unselectedTextStyle,
                onItemSelected = { newDecimal ->
                    // Use the internal state to calculate the full double
                    currentDecimal = newDecimal
                    onValueChange(currentWhole.toDouble() + (currentDecimal / 10.0))
                },
                modifier = Modifier.weight(1f)
            )
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