package net.paceapp.features.main.analyticsdetail.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import net.paceapp.core.components.AppLineChart
import net.paceapp.core.extensions.color
import net.paceapp.core.models.AnalyticsSummaryData
import net.paceapp.theme.AppColors
import net.paceapp.theme.AppTheme

@Composable
fun AnalyticDetailItem(
    modifier: Modifier,
    summaryData: AnalyticsSummaryData,
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(AppColors.White)
            .padding(16.dp),
    ) {
        val annotatedString = buildAnnotatedString {
            append(summaryData.value)
            withStyle(style = SpanStyle(fontWeight = FontWeight.Medium, fontSize = 20.sp)) {
                append(" ")
                append(summaryData.unit)
            }
        }
        //Value
        Text(
            text = annotatedString,
            style = AppTheme.typography.semiBold.copy(
                color = summaryData.type.color,
                fontSize = 28.sp,
                lineHeight = 16.sp,
                letterSpacing = 0.sp
            )
        )
        Spacer(modifier = Modifier.height(8.dp))
        //Title
        Text(
            text = summaryData.title,
            style = AppTheme.typography.semiBold.copy(
                color = AppColors.DarkCharcoal,
                fontSize = 16.sp,
                lineHeight = 16.sp,
                letterSpacing = 0.32.sp
            )
        )
        Spacer(modifier = Modifier.height(16.dp))
        //Chart
        AppLineChart(
            dataPoints = summaryData.dataPoints,
            lineColor = summaryData.type.color,
            modifier = Modifier.height(160.dp),
            yAxisFormatter = { value -> "${value.toInt()}%" },
            markersEnabled = true,
            yAxisStep = 10.0,
            minY = 0.0,
            maxY = 100.0,
        )
    }
}
