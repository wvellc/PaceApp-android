package net.paceapp.features.main.home.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kyant.backdrop.Backdrop
import com.kyant.capsule.ContinuousRoundedRectangle
import com.wvelabs.core_ui.components.LiquidGlassButton
import net.paceapp.features.main.home.models.WatchMetric
import net.paceapp.theme.AppColors
import net.paceapp.theme.AppTheme

@Composable
fun WatchMetricsButton(
    backdrop: Backdrop,
    metric: WatchMetric,
    shape: Shape = ContinuousRoundedRectangle(96.dp),
    tintColor: Color = AppColors.FluorescentMint,
    onClick: () -> Unit,
) {


    LiquidGlassButton(
        modifier = Modifier
            .width(56.dp)
            .height(115.dp),
        shape = shape,
        backdrop = backdrop,
        onClick = onClick
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(
                space = 8.dp,
                alignment = Alignment.CenterVertically
            ),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(metric.iconRes),
                contentDescription = metric.title,
                modifier = Modifier.size(32.dp),
                colorFilter = ColorFilter.tint(tintColor)
            )

            Text(
                text = metric.value,
                textAlign = TextAlign.Center,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                // Smaller value text so long values (e.g. "00:00:00") fit the narrow
                // capsule instead of getting clipped/ellipsized.
                style = AppTheme.typography.semiBold.copy(
                    color = tintColor,
                    fontSize = 12.sp,
                    lineHeight = 12.sp,
                    letterSpacing = 0.2.sp,
                )
            )

            Spacer(modifier = Modifier.height(1.dp))

            Text(
                text = metric.unit,
                textAlign = TextAlign.Center,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = AppTheme.typography.semiBold.copy(
                    color = tintColor,
                    lineHeight = 10.sp,
                    fontSize = 10.sp,
                    letterSpacing = 0.2.sp,
                )
            )
        }
    }
}