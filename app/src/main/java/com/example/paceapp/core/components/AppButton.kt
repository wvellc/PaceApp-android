package com.example.paceapp.core.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.paceapp.R
import com.example.paceapp.core.extensions.defaultClickable
import com.example.paceapp.theme.AppColors
import com.example.paceapp.theme.AppTheme
import com.kyant.capsule.ContinuousRoundedRectangle

enum class AppButtonStyle {
    OUTLINED_GRADIENT, // Your original style
    FILLED_GRADIENT,   // The new gradient background style
    FILLED_SOLID       // The new simple solid background style
}

@Composable
fun AppButton(
    modifier: Modifier,
    title: String,
    cornerShape: Shape = ContinuousRoundedRectangle(48.dp),
    iconAlignment: Alignment = Alignment.CenterEnd,
    @DrawableRes trailingIconRes: Int? = null,
    style: AppButtonStyle = AppButtonStyle.FILLED_GRADIENT,
    backgroundColor: Color = AppColors.HintGray,
    contentColor: Color = AppColors.White,
    enabled: Boolean = true,
    onClick: () -> Unit = {},
) {


    // Styling based on the style enum
    val styleModifier = when (style) {
        //Gradient outline
        AppButtonStyle.OUTLINED_GRADIENT -> Modifier
            .background(color = AppColors.Black.copy(alpha = 0.2f))
            .border(
                BorderStroke(1.dp, Brush.verticalGradient(AppColors.borderGradient)),
                shape = cornerShape
            )
            .defaultClickable(
                enable = enabled,
                rippleColor = AppColors.FluorescentMint,
                onClick = onClick
            )

        //Gradient background
        AppButtonStyle.FILLED_GRADIENT -> Modifier
            .background(brush = Brush.verticalGradient(AppColors.buttonGradient))
            .defaultClickable(
                enable = enabled,
                onClick = onClick,
                rippleColor = AppColors.White20
            )

        // Solid color background
        AppButtonStyle.FILLED_SOLID -> Modifier
            .background(color = backgroundColor)
            .defaultClickable(enable = enabled, onClick = onClick)
    }

    Box(
        modifier = modifier
            .height(54.dp)
            .clip(shape = cornerShape)
            .background(
                color = AppColors.Black.copy(alpha = 0.2f)
            )
            .alpha(alpha = if (enabled) 1f else 0.5f)
            .then(styleModifier)
            .padding(15.dp),
    ) {
        Text(
            title,
            modifier = Modifier
                .align(Alignment.Center)
                .padding(
                    start = if (trailingIconRes != null) 32.dp else 0.dp,
                    end = if (trailingIconRes != null) 32.dp else 0.dp
                ),
            style = AppTheme.typography.size16,
            fontWeight = FontWeight.Medium,
            color = contentColor,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        if (trailingIconRes != null) {
            Image(
                painter = painterResource(trailingIconRes),
                colorFilter = ColorFilter.tint(contentColor),
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
    trailingIconRes = R.drawable.ic_arrow,
    enabled = false,
    style = AppButtonStyle.FILLED_GRADIENT,
    iconAlignment = Alignment.CenterEnd
)