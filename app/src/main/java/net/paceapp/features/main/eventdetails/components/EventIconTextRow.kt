package net.paceapp.features.main.eventdetails.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import net.paceapp.theme.AppColors
import net.paceapp.theme.AppTheme

@Composable
internal fun EventIconTextRow(
    @DrawableRes iconRes: Int,
    text: String,
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        //Icon
        Image(
            painter = painterResource(iconRes),
            contentDescription = null,
            modifier = Modifier.size(24.dp),
        )
        //Text
        Text(
            text = text,
            style = AppTheme.typography.semiBold.copy(
                fontSize = 16.sp,
                lineHeight = 16.sp,
                letterSpacing = 0.32.sp,
                color = AppColors.DarkCharcoal,
            ) // Replace with your typography
        )
    }
}
