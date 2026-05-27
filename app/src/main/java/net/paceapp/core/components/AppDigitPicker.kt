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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import net.paceapp.theme.AppColors
import net.paceapp.theme.AppTheme
import kotlin.math.abs
import kotlin.math.pow
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
    selectedFontSize: TextUnit = 22.sp,
    fontSize: TextUnit = 18.sp,
    contentColor: Color = Color.White,
    trailingContentSpacing: Dp = 16.dp,
    trailingContent: @Composable (RowScope.() -> Unit)? = null
) where T : Number, T : Comparable<T> {
    val selectedTextStyle: TextStyle = AppTheme.typography.medium.copy(
        fontSize = selectedFontSize,
        color = contentColor
    )
    val unselectedTextStyle: TextStyle = AppTheme.typography.regular.copy(
        fontSize = fontSize,
        color = contentColor.copy(alpha = 0.35f)
    )
    val valDouble = initialValue.toDouble()
    val stepDouble = step.toDouble()
    val startDouble = range.start.toDouble()
    val endDouble = range.endInclusive.toDouble()

    // Safely calculate decimal precision using BigDecimal to avoid floating point quirks
    val decimalPrecision = remember(stepDouble) {
        val stepBigDecimal = stepDouble.toBigDecimal().stripTrailingZeros()
        maxOf(0, stepBigDecimal.scale())
    }

    val isDecimal = decimalPrecision > 0

    // Multiplier
    val decimalMultiplier = remember(decimalPrecision) {
        if (isDecimal) 10.0.pow(decimalPrecision).toInt() else 1
    }

    val wholeNumbers = remember(range) {
        (startDouble.toInt()..endDouble.toInt()).toList()
    }

    // Decimals list: 0..9 for 0.1, 0..99 for 0.01
    val decimals = remember(decimalMultiplier) {
        if (isDecimal) (0 until decimalMultiplier).toList() else emptyList()
    }

    // --- Calculate incoming expected parts ---
    val incomingWhole = valDouble.toInt()
    val incomingDecimal = remember(valDouble, decimalMultiplier) {
        if (isDecimal) {
            val fraction = abs(valDouble - incomingWhole)
            (fraction * decimalMultiplier).roundToInt().coerceIn(0, decimalMultiplier - 1)
        } else 0
    }

    // --- Internal State ---
    var currentWhole by remember { mutableIntStateOf(incomingWhole) }
    var currentDecimal by remember { mutableIntStateOf(incomingDecimal) }

    var recomposeKey by remember { mutableIntStateOf(0) }

    // Watch for incoming changes from the ViewModel (like when DB finishes loading)
    LaunchedEffect(initialValue) {
        val calculatedCurrent =
            currentWhole.toDouble() + (currentDecimal.toDouble() / decimalMultiplier)

        // Adjusted epsilon to support 0.01 accuracy
        if (abs(valDouble - calculatedCurrent) > 0.0001) {
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
                    onValueChange(currentWhole.toDouble() + (currentDecimal.toDouble() / decimalMultiplier))
                },
                modifier = Modifier.weight(1f)
            )

            if (isDecimal) {
                // --- Separator ---
                Box(
                    modifier = Modifier
                        .padding(horizontal = 8.dp)
                        .size(6.dp)
                        .background(contentColor, CircleShape)
                )

                // --- Decimal Wheel ---
                GenericWheelPicker(
                    items = decimals,
                    itemHeight = itemHeight,
                    visibleItemsCount = visibleItems,
                    initialIndex = currentDecimal,
                    // 4. Pad strings dynamically: padStart(2) for 0.01, padStart(1) for 0.1
                    itemLabel = { it.toString().padStart(decimalPrecision, '0') },
                    spacing = itemSpacing,
                    selectorBackground = selectedItemBackground,
                    selectedStyle = selectedTextStyle,
                    unselectedStyle = unselectedTextStyle,
                    onItemSelected = { newDecimal ->
                        currentDecimal = newDecimal
                        onValueChange(currentWhole.toDouble() + (currentDecimal.toDouble() / decimalMultiplier))
                    },
                    modifier = Modifier.weight(1f)
                )
            }
            if (trailingContent != null) {
                Spacer(modifier = Modifier.width(trailingContentSpacing))
                trailingContent()
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