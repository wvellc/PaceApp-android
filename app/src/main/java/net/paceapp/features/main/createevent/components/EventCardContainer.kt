package net.paceapp.features.main.createevent.components

import androidx.annotation.StringRes
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import net.paceapp.R
import net.paceapp.core.extensions.defaultClickable
import net.paceapp.theme.AppColors
import net.paceapp.theme.AppTheme

@Composable
internal fun EventCardContainer(
    modifier: Modifier = Modifier,
    showInfo: Boolean = false,
    onInfoClick: () -> Unit = {},
    @StringRes titleRes: Int? = null,
    content: @Composable ColumnScope.() -> Unit = {},
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(AppColors.White)
            .animateContentSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        //Top image
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 260.dp)
                .weight(1f, fill = false)
                .clip(RoundedCornerShape(8.dp))
        ) {
            //Image
            Image(
                painter = painterResource(R.drawable.create_event_card_bg),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

            //Info icon
            if (showInfo) {
                Icon(
                    painter = painterResource(R.drawable.ic_info),
                    contentDescription = null,
                    modifier = Modifier
                        .clip(CircleShape)
                        .defaultClickable(onClick = onInfoClick)
                        .align(Alignment.TopEnd)
                        .padding(8.dp),
                    tint = AppColors.FluorescentMint,
                )
            }
        }
        //Title
        titleRes?.let { id ->
            Text(
                text = stringResource(id), style = AppTheme.typography.semiBold.copy(
                    color = AppColors.DarkCharcoal,
                    fontSize = 24.sp,
                    lineHeight = 24.sp,
                    letterSpacing = 0.54.sp
                )
            )
        }

        //Content
        content()
    }
}