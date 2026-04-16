package com.example.paceapp.core.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.paceapp.R
import com.example.paceapp.ui.theme.AppColors
import com.example.paceapp.ui.theme.AppTheme
import com.kyant.capsule.ContinuousRoundedRectangle

@Composable
fun AppButton(
    modifier: Modifier,
    title: String,
    cornerShape: Shape = ContinuousRoundedRectangle(48.dp),
    iconAlignment: Alignment = Alignment.CenterEnd,
    @DrawableRes iconRes: Int? = null,
    onClick: () -> Unit = {},

    ) {

    Box(
        modifier = modifier
            .height(54.dp)
            .clip(shape = cornerShape)
            .background(
                color = AppColors.Black.copy(alpha = 0.2f)
            )
            .border(
                BorderStroke(
                    1.dp,
                    Brush.verticalGradient(AppColors.borderGradient),
                ),
                shape = cornerShape,
            )
            .clickable(
                indication = ripple(bounded = true, color = AppColors.FluorescentMint),
                interactionSource = remember { MutableInteractionSource() },
                onClick = onClick
            )
            .padding(15.dp)
    ) {
        Text(
            title,
            modifier = Modifier
                .align(Alignment.Center)
                .padding(
                    start = if (iconRes != null) 32.dp else 0.dp,
                    end = if (iconRes != null) 32.dp else 0.dp
                ),
            style = AppTheme.typography.size16,
            fontWeight = FontWeight.Medium,
            color = AppColors.White,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        if (iconRes != null) {
            Image(
                painter = painterResource(iconRes),
                modifier = Modifier
                    .align(iconAlignment)
                    .size(32.dp),
                contentDescription = null,
            )
        }
    }
}

@Composable
@Preview
fun ButtonPreview() = AppButton(
    modifier = Modifier.fillMaxWidth(),
    title = "Lorem ipsum dolor",
    iconRes = R.drawable.ic_arrow,
    iconAlignment = Alignment.CenterEnd
)