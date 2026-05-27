package net.paceapp.features.main.createevent.steps

import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kyant.capsule.ContinuousRoundedRectangle
import net.paceapp.core.components.AppDigitPicker
import net.paceapp.core.components.AppSegmentedButtons
import net.paceapp.core.domain.models.DistanceModel
import net.paceapp.core.enums.DistanceUnits
import net.paceapp.core.extensions.g2Continuity
import net.paceapp.features.main.createevent.components.EventCardContainer
import net.paceapp.features.main.createevent.components.SelectedPickerItemBg
import net.paceapp.theme.AppColors

@Composable
internal fun DistanceStep(
    @StringRes titleRes: Int? = null,
    distance: DistanceModel,
    onDistanceChanged: (DistanceModel) -> Unit
) {
    //Card container
    EventCardContainer(titleRes = titleRes,) {
        //Distance units
        AppSegmentedButtons(
            modifier = Modifier
                .fillMaxWidth()
                .clip(ContinuousRoundedRectangle(12.dp, continuity = g2Continuity))
                .background(AppColors.HintGray)
                .padding(4.dp),
            segments = DistanceUnits.entries,
            unselectedTextColor = AppColors.DarkCharcoal,
            selectedTextColor = AppColors.White,
            selectedSegment = distance.unit,
            itemTitle = { stringResource(it.titleRes) },
            onSegmentSelected = { unit ->
                onDistanceChanged(
                    distance.copy(unit = unit)

                )
            },
        )

        //Digit picker
        AppDigitPicker(
            modifier = Modifier
                .border(
                    width = 1.dp,
                    color = AppColors.HintGray,
                    shape = ContinuousRoundedRectangle(12.dp)
                )
                .padding(horizontal = 24.dp, vertical = 8.dp),
            initialValue = distance.value.toDouble(),
            onValueChange = { newValue ->
                onDistanceChanged(distance.copy(value = newValue.toFloat()))
            },
            range = 1.0..999.0,
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
}


