package net.paceapp.features.main.eventdetails.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kyant.backdrop.backdrops.LayerBackdrop
import com.kyant.capsule.ContinuousCapsule
import com.wvelabs.core_ui.components.LiquidGlassButton
import net.paceapp.theme.AppTheme

@Composable
internal fun EventDetailButton(
    modifier: Modifier = Modifier,
    iconRes: Int,
    contentColor: Color,
    label: String,
    backdrop: LayerBackdrop,
    onClick: () -> Unit = {},
) {
    LiquidGlassButton(
        modifier = modifier.height(54.dp),
        shape = ContinuousCapsule,
        backdrop = backdrop,
        onClick = onClick,
    ) {
        Row(
            modifier = Modifier
                .matchParentSize(),
            horizontalArrangement = Arrangement.spacedBy(
                8.dp,
                alignment = Alignment.CenterHorizontally
            ),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            //Icon
            Icon(
                painter = painterResource(iconRes),
                contentDescription = null,
                modifier = Modifier.height(24.dp),
                tint = contentColor
            )
            //Text
            Text(
                text = label,
                style = AppTheme.typography.semiBold.copy(
                    fontSize = 16.sp,
                    lineHeight = 16.sp,
                    letterSpacing = 0.32.sp,
                    color = contentColor,
                )
            )
        }

    }
}
