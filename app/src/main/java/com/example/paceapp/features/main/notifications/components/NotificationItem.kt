package com.example.paceapp.features.main.notifications.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
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
import com.example.paceapp.R
import com.example.paceapp.core.extensions.defaultClickable
import com.example.paceapp.features.main.notifications.models.NotificationUI
import com.example.paceapp.theme.AppColors
import com.example.paceapp.theme.AppTheme

@Composable
fun NotificationItem(
    modifier: Modifier,
    model: NotificationUI
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(AppColors.White)
            .defaultClickable {}
            .padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.Top,
    ) {
        //Icon
        Image(
            painter = painterResource(R.drawable.ic_notifications),
            contentDescription = stringResource(R.string.notifications),
            modifier = Modifier.size(42.dp)
        )
        //Notification details
        Column(
            modifier = Modifier.weight(1f),
            horizontalAlignment = Alignment.Start
        ) {
            //Title
            Text(
                text = model.title,
                style = AppTheme.typography.semiBold.copy(
                    fontSize = 17.sp,
                    lineHeight = 17.sp,
                    letterSpacing = 0.sp,
                    color = AppColors.DarkCharcoal
                )
            )
            Spacer(modifier = Modifier.height(2.dp))
            //Message
            Text(
                text = model.message,
                style = AppTheme.typography.medium.copy(
                    fontSize = 13.sp,
                    lineHeight = 13.sp,
                    letterSpacing = 0.sp,
                    color = AppColors.FashionGray
                )
            )
            Spacer(modifier = Modifier.height(8.dp))

            //Time
            Text(
                text = model.timeAgo,
                style = AppTheme.typography.medium.copy(
                    fontSize = 13.sp,
                    lineHeight = 13.sp,
                    letterSpacing = 0.sp,
                    color = AppColors.FashionGray
                )
            )

        }
    }
}
