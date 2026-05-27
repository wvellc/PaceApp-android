package net.paceapp.features.main.createevent.steps

import androidx.annotation.StringRes
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kyant.capsule.ContinuousRoundedRectangle
import net.paceapp.R
import net.paceapp.core.components.AppDigitPicker
import net.paceapp.core.components.AppDurationPicker
import net.paceapp.features.main.createevent.components.EventCardContainer
import net.paceapp.features.main.createevent.components.SelectedPickerItemBg
import net.paceapp.features.main.createevent.models.RunSegment
import net.paceapp.theme.AppColors
import net.paceapp.theme.AppTheme

@Composable
internal fun SegmentDetailsStep(
    @StringRes titleRes: Int? = null,
    @StringRes distanceUnitRes: Int,
    segment: RunSegment,
    segmentError: String? = null,
    onSegmentUpdated: (RunSegment) -> Unit,

    ) {
    EventCardContainer(
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = stringResource(R.string.segment_distance_title, segment.id),
                style = AppTheme.typography.semiBold.copy(
                    color = AppColors.DarkCharcoal,
                    fontSize = 20.sp,
                    lineHeight = 16.sp,
                )
            )

            //Digit picker
            AppDigitPicker(
                modifier = Modifier
                    .border(
                        width = 1.dp,
                        color = AppColors.HintGray,
                        shape = ContinuousRoundedRectangle(12.dp)
                    )
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                initialValue = segment.distance.toDouble(),
                onValueChange = { newValue ->
                    onSegmentUpdated(segment.copy(distance = newValue.toFloat()))
                },
                trailingContent = {
                    Text(
                        text = stringResource(id = distanceUnitRes),
                        style = AppTheme.typography.medium.copy(
                            fontSize = 16.sp,
                            color = AppColors.DarkCharcoal
                        )
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    Image(
                        painter = painterResource(R.drawable.ic_segement_distance),
                        contentDescription = null
                    )
                },
                range = 0.1..999.0,
                step = 0.01,
                selectedItemBackground = { SelectedPickerItemBg() },
                itemSpacing = 4.dp,
                itemHeight = 20.dp,
                padWithZero = true,
                selectedFontSize = 16.sp,
                fontSize = 12.sp,
                contentColor = AppColors.DarkCharcoal
            )
        }

        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {

            if (titleRes != null) {
                Text(
                    text = stringResource(titleRes, segment.id),
                    style = AppTheme.typography.semiBold.copy(
                        color = AppColors.DarkCharcoal,
                        fontSize = 20.sp,
                        lineHeight = 16.sp,
                    )
                )
            }
            //Duration picker
            AppDurationPicker(
                modifier = Modifier
                    .border(
                        width = 1.dp,
                        color = AppColors.HintGray,
                        shape = ContinuousRoundedRectangle(12.dp)
                    )
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                initialDurationSeconds = segment.durationInSeconds,
                trailingIcon = {
                    Image(
                        painter = painterResource(R.drawable.ic_clock),
                        contentDescription = null,
                    )
                },
                onDurationChange = { duration ->
                    onSegmentUpdated(segment.copy(durationInSeconds = duration))
                },
                selectedItemBackground = { SelectedPickerItemBg() },
                itemSpacing = 4.dp,
                itemHeight = 20.dp,
                selectedFontSize = 16.sp,
                fontSize = 12.sp,
                contentColor = AppColors.DarkCharcoal
            )
        }

        AnimatedContent(
            targetState = segmentError != null
        ) { showError ->
            if (showError) {
                Text(
                    segmentError!!, style = AppTheme.typography.medium.copy(
                        fontSize = 14.sp,
                        color = AppColors.Error
                    )
                )

            }
        }
    }
}
