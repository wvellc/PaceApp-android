package net.paceapp.features.main.analytics.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import net.paceapp.core.components.AppLineChart
import net.paceapp.core.extensions.defaultClickable
import net.paceapp.core.extensions.color
import net.paceapp.core.extensions.iconRes
import net.paceapp.core.extensions.titleRes
import net.paceapp.core.models.AnalyticsSummaryData
import net.paceapp.theme.AppColors
import net.paceapp.theme.AppTheme

@Composable
fun AnalyticsListItem(
    modifier: Modifier,
    analytics: AnalyticsSummaryData,
    onClick: () -> Unit
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(AppColors.White)
            .defaultClickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        //Icon and Title
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            //Icon
            Image(
                painter = painterResource(analytics.type.iconRes),
                contentDescription = stringResource(analytics.type.titleRes),
                modifier = Modifier.size(32.dp),
                colorFilter = ColorFilter.tint(analytics.type.color)
            )
            //Title
            Text(
                text = stringResource(analytics.type.titleRes), style = AppTheme.typography.semiBold.copy(
                    color = AppColors.DarkCharcoal,
                    fontSize = 16.sp,
                    lineHeight = 16.sp,
                    letterSpacing = 0.32.sp
                )
            )
        }

        //Chart
        AppLineChart(
            dataPoints = analytics.dataPoints,
            lineColor = analytics.type.color,
            modifier = Modifier.height(120.dp),
            yAxisFormatter = { value -> "${value.toInt()}%" },
            yAxisStep = 25.0,
            minY = 0.0,
            maxY = 100.0,
        )
    }
}
