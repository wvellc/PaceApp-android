package net.paceapp.core.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.RangeSlider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import net.paceapp.R
import net.paceapp.theme.AppColors
import net.paceapp.theme.AppTheme
import net.paceapp.theme.PaceAppTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppRangeSlider(
    modifier: Modifier = Modifier,
    value: ClosedFloatingPointRange<Float>,
    onValueChange: (ClosedFloatingPointRange<Float>) -> Unit,
    valueRange: ClosedFloatingPointRange<Float> = 0f..150f,
    unitLabel: String = "mi"
) {
    // MaxOf prevents negative values
    val calculatedSteps = maxOf(0, (valueRange.endInclusive - valueRange.start).toInt() - 1)

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        RangeSlider(
            value = value,
            onValueChange = onValueChange,
            valueRange = valueRange,
            steps = calculatedSteps,
            track = {
                SliderDefaults.Track(
                    rangeSliderState = it,
                    modifier = Modifier.height(8.dp),
                    drawStopIndicator = {},
                    drawTick = { _, _ -> },
                    thumbTrackGapSize = 0.dp,
                    trackInsideCornerSize = 0.dp,
                    colors = SliderDefaults.colors(
                        activeTrackColor = AppColors.NeonAquaBlue,
                        inactiveTrackColor = AppColors.HintGray,
                        activeTickColor = Color.Transparent,
                        inactiveTickColor = Color.Transparent
                    )
                )
            },
            startThumb = { SliderThumb() },
            endThumb = { SliderThumb() },
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            SliderLabel(
                value = value.start.toInt(),
                title = stringResource(R.string.min),
                unitLabel = unitLabel,
                alignment = Alignment.Start
            )

            SliderLabel(
                value = value.endInclusive.toInt(),
                title = stringResource(R.string.max),
                unitLabel = unitLabel,
                alignment = Alignment.End
            )
        }
    }
}

@Composable
private fun SliderThumb() {
    Image(
        modifier = Modifier
            .size(32.dp)
            .border(
                border = BorderStroke(4.dp, color = AppColors.NeonAquaBlue),
                shape = CircleShape
            )
            .background(AppColors.White, shape = CircleShape),
        painter = painterResource(id = R.drawable.ic_slider_thumb),
        contentDescription = null
    )
}

@Composable
private fun SliderLabel(
    value: Int,
    title: String,
    unitLabel: String,
    alignment: Alignment.Horizontal
) {
    Column(horizontalAlignment = alignment) {
        Text(
            text = "$value $unitLabel",
            style = AppTheme.typography.semiBold.copy(
                color = AppColors.DarkCharcoal,
                fontSize = 20.sp,
                lineHeight = 24.sp
            )
        )
        Text(
            text = title,
            style = AppTheme.typography.medium.copy(
                color = AppColors.FashionGray,
                fontSize = 16.sp,
                lineHeight = 24.sp
            )
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun AppRangeSliderPreview() {
    PaceAppTheme {
        var sliderValue by remember { mutableStateOf(20f..80f) }
        Column(modifier = Modifier.padding(16.dp)) {
            AppRangeSlider(
                value = sliderValue,
                onValueChange = { sliderValue = it }
            )
        }
    }
}