package com.example.paceapp.features.main.profile.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.dropShadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.paceapp.R
import com.example.paceapp.core.extensions.defaultClickable
import com.example.paceapp.theme.AppColors
import com.example.paceapp.theme.AppTheme

@Composable
fun EditProfileButton(
    shape: RoundedCornerShape = RoundedCornerShape(8.dp),
    onClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .height(42.dp)
            .width(150.dp)
            .border(
                shape = shape,
                border = BorderStroke(
                    width = 1.dp,
                    brush = Brush.verticalGradient(colors = AppColors.bottomTabBorderGradient),
                ),
            )
            .dropShadow(
                shape = shape, shadow = Shadow(
                    radius = 4.dp,
                    spread = 0.dp,
                    color = AppColors.Black,
                    alpha = 0.25f,
                    offset = DpOffset(0.dp, 4.dp),
                )
            )
            .clip(shape)
            .background(AppColors.FluorescentMint.copy(alpha = 0.2f))
            .defaultClickable(
                onClick = onClick,
                interactionSource = remember { MutableInteractionSource() },
                rippleColor = AppColors.FluorescentMint
            ),
    ) {
        Text(
            stringResource(R.string.edit_profile),
            modifier = Modifier.align(Alignment.Center),
            style = AppTheme.typography.semiBold.copy(
                fontSize = 16.sp,
                color = AppColors.FluorescentMint,
            ),
        )
    }
}


@Preview
@Composable
fun ButtonPreview() =
    Box(
        modifier = Modifier
            .background(color = AppColors.RadiantBlue)
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        EditProfileButton() {}
    }