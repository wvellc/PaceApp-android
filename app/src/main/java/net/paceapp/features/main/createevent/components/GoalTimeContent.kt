package net.paceapp.features.main.createevent.components

import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kyant.capsule.ContinuousRoundedRectangle
import net.paceapp.R
import net.paceapp.core.components.AppDurationPicker
import net.paceapp.theme.AppColors

@Composable
fun GoalTimeContent(
    @StringRes titleRes: Int? = null,
    duration: Long,
    onDurationChange: (Long) -> Unit
) {
    EventCardContainer(titleRes = titleRes) {

        AppDurationPicker(
            modifier = Modifier
                .border(
                    width = 1.dp,
                    color = AppColors.HintGray,
                    shape = ContinuousRoundedRectangle(12.dp)
                )
                .padding(horizontal = 16.dp, vertical = 8.dp),
            initialDurationSeconds = duration,
            trailingIcon = {
                Image(
                    painter = painterResource(R.drawable.ic_clock),
                    contentDescription = null,
                )
            },
            onDurationChange = onDurationChange,
            selectedItemBackground = { SelectedPickerItemBg() },
            itemSpacing = 4.dp,
            itemHeight = 22.dp,
            selectedFontSize = 16.sp,
            fontSize = 12.sp,
            contentColor = AppColors.DarkCharcoal
        )
    }
}