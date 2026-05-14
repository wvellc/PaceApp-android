package com.example.paceapp.features.main.profile.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import com.example.paceapp.core.extensions.defaultClickable
import com.example.paceapp.features.main.profile.enums.ProfileOptions
import com.example.paceapp.theme.AppColors
import com.example.paceapp.theme.AppTheme
import com.kyant.backdrop.backdrops.rememberLayerBackdrop
import com.wvelabs.core_ui.components.LiquidSwitch
import com.wvelabs.core_ui.extensions.advancedShadow

@Composable
fun ProfileOptionItem(
    option: ProfileOptions,
    isChecked: Boolean = false,
    onToggle: (Boolean) -> Unit = {},
    shape: RoundedCornerShape = RoundedCornerShape(8.dp),
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .background(color = AppColors.White)
            .defaultClickable(onClick = onClick)
            .padding(8.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        //Icon
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
        //Switch
        if (option.showSwitch) {
            LiquidSwitch(
                selected = { isChecked },
                switchColor = AppColors.NeonAquaBlue,
                trackColor = AppColors.ShipGray30,
                onSelect = onToggle,
                backdrop = rememberLayerBackdrop(),
            )
        }
    }
}