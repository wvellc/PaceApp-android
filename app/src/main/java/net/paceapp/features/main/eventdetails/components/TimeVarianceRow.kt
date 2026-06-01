package net.paceapp.features.main.eventdetails.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wvelabs.core_ui.utils.DateTimeHelper
import net.paceapp.R
import net.paceapp.theme.AppColors
import net.paceapp.theme.AppTheme
import kotlin.time.Duration.Companion.seconds

@Composable
fun TimeVarianceRow(
    modifier: Modifier = Modifier,
    varianceInSeconds: Long,
    percentage: Int,
    isAheadOfTime: Boolean = false,
) {
    val isPositive = varianceInSeconds >= 0

    val bgColor = if (isAheadOfTime) {
        AppColors.FluorescentMint
    } else {
        AppColors.Error
    }

    val contentColor = if (isAheadOfTime) AppColors.DarkCharcoal else AppColors.White

    val formattedTime = DateTimeHelper.formatDuration(
        duration = varianceInSeconds.seconds,
        forceShowHours = false
    )
    val timeString = when {
        isPositive -> "+$formattedTime"
        else -> formattedTime
    }

    Row(
        modifier = modifier
            .clip(RoundedCornerShape(50)) // Pill shape
            .background(bgColor)
            .padding(horizontal = 8.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        //Icon
        Icon(
            painter = painterResource(R.drawable.ic_clock),
            contentDescription = null,
            modifier = Modifier.size(24.dp),
            tint = contentColor
        )

        //Time
        Text(
            text = timeString,
            color = contentColor,
            modifier = Modifier.weight(1f),
            style = AppTheme.typography.semiBold.copy(
                fontSize = 20.sp,
                lineHeight = 20.sp,
                letterSpacing = 0.4.sp
            ) // Replace with your typography
        )

        //Percentage
        Text(
            text = "$percentage%",
            color = contentColor,
            style = AppTheme.typography.semiBold.copy(
                fontSize = 20.sp,
                lineHeight = 20.sp,
                letterSpacing = 0.4.sp
            )
        )

    }
}