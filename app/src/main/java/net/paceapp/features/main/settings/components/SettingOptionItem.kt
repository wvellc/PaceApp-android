package net.paceapp.features.main.settings.components

import androidx.compose.animation.animateContentSize
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import net.paceapp.core.extensions.defaultClickable
import net.paceapp.features.main.settings.enums.SettingOptions
import net.paceapp.theme.AppColors
import net.paceapp.theme.AppTheme
import com.wvelabs.core_ui.extensions.defaultAnimSpec

@Composable
fun SettingOptionItem(
    option: SettingOptions,
    shape: RoundedCornerShape = RoundedCornerShape(8.dp),
    onClick: () -> Unit,
    isDevOptionExpanded: Boolean = false,
    onDevButtonTap: () -> Unit = {}, // Callback for the website button
) {
    // Local state to track expansion for DEVELOPED_BY
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .background(color = AppColors.White)
            .defaultClickable(onClick = onClick)
            .animateContentSize(
                defaultAnimSpec(100)
            )
    ) {
        // Option
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            //Image
            Image(
                modifier = Modifier.size(48.dp),
                contentDescription = null,
                painter = painterResource(option.iconRes),
            )
            //Title
            Text(
                modifier = Modifier.weight(1f),
                text = stringResource(option.titleRes),
                style = AppTheme.typography.semiBold.copy(
                    color = AppColors.DarkCharcoal,
                    fontSize = 16.sp,
                )
            )
        }

        // The Expandable Developed By Details
        if (option == SettingOptions.DEVELOPED_BY) {
            DevelopedByDetails(
                isDevOptionExpanded = isDevOptionExpanded,
                onDevButtonTap = onDevButtonTap
            )
        }
    }
}
