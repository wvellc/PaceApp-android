package net.paceapp.features.main.createevent.steps

import androidx.annotation.StringRes
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kyant.capsule.ContinuousRoundedRectangle
import net.paceapp.R
import net.paceapp.core.components.AppDigitPicker
import net.paceapp.core.components.AppDropdown
import net.paceapp.features.main.createevent.components.EventCardContainer
import net.paceapp.features.main.createevent.components.SelectedPickerItemBg
import net.paceapp.features.main.createevent.enums.EventType
import net.paceapp.features.main.createevent.extensions.labelRes
import net.paceapp.theme.AppColors
import net.paceapp.theme.AppTheme

@Composable
internal fun LookBackIntervalStep(
    @StringRes titleRes: Int? = null,
    lookBackInterval: Int,
    maxInterval: Int,
    eventType: EventType,
    onEventTypeUpdated: (EventType) -> Unit,
    onIntervalUpdated: (Int) -> Unit,
) {
    EventCardContainer(
        modifier = Modifier
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            //Look-back interval title
            titleRes?.let {
                Text(
                    text = stringResource(it),
                    style = AppTheme.typography.semiBold.copy(
                        color = AppColors.DarkCharcoal,
                        fontSize = 20.sp,
                        lineHeight = 16.sp,
                    )
                )
            }

            //Look-back interval
            AppDigitPicker(
                modifier = Modifier
                    .border(
                        width = 1.dp,
                        color = AppColors.HintGray,
                        shape = ContinuousRoundedRectangle(12.dp)
                    )
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                initialValue = lookBackInterval,
                onValueChange = { onIntervalUpdated(it.toInt()) },
                range = 1..maxInterval,
                step = 1,
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
            //Event type title
            Text(
                text = stringResource(R.string.event_type),
                style = AppTheme.typography.semiBold.copy(
                    color = AppColors.DarkCharcoal,
                    fontSize = 20.sp,
                    lineHeight = 16.sp,
                )
            )

            //Event type
            AppDropdown(
                items = EventType.entries,
                selectedItem = eventType,
                onItemSelected = onEventTypeUpdated,
                itemTitleExtractor = { stringResource(it.labelRes) },
                modifier = Modifier
                    .border(
                        width = 1.dp,
                        color = AppColors.FashionGray,
                        shape = RoundedCornerShape(12.dp)
                    ),
                fieldShape = RoundedCornerShape(12.dp),
            )
        }

    }
}
