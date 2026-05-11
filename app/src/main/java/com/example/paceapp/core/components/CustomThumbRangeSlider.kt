package com.example.paceapp.core.components

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.RangeSlider
import androidx.compose.material3.SliderDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.example.paceapp.theme.AppColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomThumbRangeSlider(
    value: ClosedFloatingPointRange<Float>,
    onValueChange: (ClosedFloatingPointRange<Float>) -> Unit,
    modifier: Modifier = Modifier,
    valueRange: ClosedFloatingPointRange<Float> = 0f..100f,
    startThumb: @Composable () -> Unit,
    endThumb: @Composable () -> Unit
) {
    RangeSlider(
        value = value,
        onValueChange = onValueChange,
        valueRange = valueRange,
        modifier = modifier,
        startThumb = {
            startThumb()
        },
        endThumb = {
            endThumb()
        },
        colors = SliderDefaults.colors(
            activeTrackColor = AppColors.White,
            inactiveTrackColor = AppColors.HintGray,
            activeTickColor = Color.Transparent,
            inactiveTickColor = Color.Transparent
        )
    )
}