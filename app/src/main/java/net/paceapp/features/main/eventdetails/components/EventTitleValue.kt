package net.paceapp.features.main.eventdetails.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import net.paceapp.theme.AppColors
import net.paceapp.theme.AppTheme

@Composable
internal fun EventTitleValue(
    title: String,
    value: String
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        //Title
        Text(
            text = title,
            style = AppTheme.typography.regular.copy(
                color = AppColors.FashionGray,
                fontSize = 13.sp,
                lineHeight = 13.sp,
                letterSpacing = 0.26.sp

            )
        )
        //Value
        Text(
            text = value,
            style = AppTheme.typography.semiBold.copy(
                color = AppColors.DarkCharcoal,
                fontSize = 17.sp,
                lineHeight = 17.sp,
                letterSpacing = 0.34.sp

            )
        )
    }
}