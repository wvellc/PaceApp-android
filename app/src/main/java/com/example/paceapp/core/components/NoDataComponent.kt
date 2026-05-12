package com.example.paceapp.core.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.paceapp.core.extensions.defaultClickable
import com.example.paceapp.theme.AppColors
import com.example.paceapp.theme.AppTheme

@Composable
fun NoDataView(
    modifier: Modifier = Modifier,
    title: String,
    subtitle: String? = null,
    @DrawableRes imageRes: Int? = null,
    imageShape: Shape = RectangleShape,
    buttonLabel: String? = null,
    onImageClick: (() -> Unit)? = null,
    onButtonClick: () -> Unit = {}
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(AppTheme.screenPadding),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        // Top padding
        Spacer(modifier = Modifier.fillMaxHeight(0.2f))

        if (imageRes != null) {
            // Logo/Icon
            Image(
                painter = painterResource(id = imageRes),
                contentDescription = null,
                modifier = Modifier
                    .size(180.dp)
                    .clip(imageShape)
                    .defaultClickable(
                        onClick = onImageClick ?: {},
                        enabled = onImageClick != null
                    ),
            )

            Spacer(modifier = Modifier.height(34.dp))
        }


        // Title
        Text(
            text = title,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            textAlign = TextAlign.Center,
            style = AppTheme.typography.size24.copy(
                color = AppColors.White,
                fontWeight = FontWeight.SemiBold,
                lineHeight = 24.sp,
            )
        )

        if (subtitle != null) {
            Spacer(modifier = Modifier.height(8.dp))

            // Subtitle
            Text(
                text = subtitle,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                textAlign = TextAlign.Center,
                style = AppTheme.typography.size16.copy(
                    color = AppColors.White,
                    fontWeight = FontWeight.Medium,
                )
            )
        }

        // Optional Button Slot
        if (buttonLabel != null) {
            // This weight spacer pushes the button to the bottom of the screen
            Spacer(modifier = Modifier.weight(1f))

            AppButton(
                modifier = Modifier.fillMaxWidth(),
                title = buttonLabel,
                onClick = onButtonClick,
            )
        }
    }
}