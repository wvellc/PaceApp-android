package net.paceapp.features.main.createevent.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import net.paceapp.theme.AppColors

@Composable
fun SelectedPickerItemBg() {
    Box(
        modifier = Modifier
            .width(100.dp)
            .fillMaxHeight()
            .clip(RoundedCornerShape(50))
            .background(AppColors.HintGray)
    )
}