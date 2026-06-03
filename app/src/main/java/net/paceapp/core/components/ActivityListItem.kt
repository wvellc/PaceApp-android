package net.paceapp.core.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import net.paceapp.R
import net.paceapp.core.extensions.defaultClickable
import net.paceapp.core.models.ActivityUiModel
import net.paceapp.theme.AppColors
import net.paceapp.theme.AppTheme

@Composable
fun ActivityListItem(
    modifier: Modifier = Modifier,
    history: ActivityUiModel,
    onClick: () -> Unit = {},
) {

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(shape = RoundedCornerShape(16.dp))
            .background(
                color = AppColors.White,
            )
            .defaultClickable(onClick = onClick)
            .padding(vertical = 8.dp, horizontal = 16.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_runner),
                contentDescription = "Run Icon",
                modifier = Modifier,
            )

            RunStatItem(
                label = history.title,
                value = history.date,
            )

            // Run status
            RunStat(
                paceDifference = history.paceDifference,
                isPaceImproved = history.isAheadOfTime
            )

        }
        Spacer(modifier = Modifier.height(8.dp))

        // Stats Row (Distance, Time, Avg PACE)
        Row(
            modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween
        ) {
            RunStatItem(
                label = "Distance",
                value = history.distance,

                )
            RunStatItem(
                label = "Time",
                value = history.goalTime,

                )
            RunStatItem(
                label = "Avg PACE",
                value = history.avgPace,

                )
        }
    }
}

@Composable
private fun RunStat(
    paceDifference: String, isPaceImproved: Boolean = false
) {
    val (backgroundColor, textColor) = when {
        isPaceImproved -> Pair(AppColors.FluorescentMint, AppColors.DarkCharcoal)
        else -> Pair(AppColors.Error, AppColors.White)
    }
    Box(
        modifier = Modifier
            .background(
                color = backgroundColor,
                shape = RoundedCornerShape(40.dp)
            )
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(
            text = paceDifference,
            style = AppTheme.typography.semiBold.copy(
                fontSize = 11.sp,
                color = textColor,
                lineHeight = 11.sp,
                letterSpacing = 0.22.sp,
            ),
        )
    }
}

@Composable
private fun RowScope.RunStatItem(
    modifier: Modifier = Modifier,
    label: String,
    value: String,
    titleStyle: TextStyle = AppTheme.typography.regular.copy(
        fontSize = 13.sp,
        lineHeight = 13.sp,
        letterSpacing = 0.26.sp,
        color = AppColors.FashionGray
    ),
    valueStyle: TextStyle = AppTheme.typography.semiBold.copy(
        fontSize = 17.sp,
        lineHeight = 17.sp,
        letterSpacing = 0.34.sp,
    ),
) {
    Column(
        modifier = modifier.weight(1f),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Text(
            text = label,
            style = titleStyle,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Text(
            text = value,
            style = valueStyle,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

