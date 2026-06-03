package net.paceapp.features.main.home.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import net.paceapp.R
import net.paceapp.core.extensions.defaultClickable
import net.paceapp.core.models.ActivityUiModel
import net.paceapp.theme.AppColors
import net.paceapp.theme.AppTheme

@Composable
fun UpcomingActivityListItem(
    modifier: Modifier = Modifier,
    model: ActivityUiModel,
    onClick: () -> Unit
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(AppColors.White)
            .defaultClickable(onClick = onClick)
            .padding(16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.Top
    ) {
        // Icon
        Image(
            painter = painterResource(id = R.drawable.ic_runner),
            contentDescription = "Activity Icon",
            modifier = Modifier.size(38.dp)
        )

        // The Data Grid
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Left Data Column
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                //Name and Date
                ActivityDataBlock(
                    label = model.title,
                    value = model.date
                )
                //Location
                ActivityDataBlock(
                    label = stringResource(R.string.location),
                    value = model.location
                )
            }

            // Right Data Column
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                //Distance
                ActivityDataBlock(
                    label = stringResource(R.string.distance),
                    value = model.distance
                )
                //Goal time
                ActivityDataBlock(
                    label = stringResource(R.string.goal_time),
                    value = model.goalTime
                )
            }
        }
    }
}

// A reusable mini-component for grid data
@Composable
private fun ActivityDataBlock(
    label: String,
    value: String
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = label,
            style = AppTheme.typography.regular.copy(
                fontSize = 13.sp,
                color = AppColors.FashionGray,
            )
        )
        Text(
            text = value,
            style = AppTheme.typography.semiBold.copy(
                fontSize = 17.sp,
                color = AppColors.DarkCharcoal
            )
        )
    }
}