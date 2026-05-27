package net.paceapp.features.main.createevent.steps

import androidx.annotation.StringRes
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kyant.capsule.ContinuousRoundedRectangle
import net.paceapp.core.components.AppDigitPicker
import net.paceapp.features.main.createevent.components.EventCardContainer
import net.paceapp.features.main.createevent.components.SelectedPickerItemBg
import net.paceapp.theme.AppColors

@Composable
internal fun SegmentCountStep(
    @StringRes titleRes: Int? = null,
    count: Int,
    onCountChange: (Double) -> Unit
) {
    EventCardContainer(titleRes = titleRes) {
        AppDigitPicker(
            modifier = Modifier
                .border(
                    width = 1.dp,
                    color = AppColors.HintGray,
                    shape = ContinuousRoundedRectangle(12.dp)
                )
                .padding(horizontal = 24.dp, vertical = 8.dp),
            initialValue = count,
            onValueChange = onCountChange,
            range = 2..20,
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
}

