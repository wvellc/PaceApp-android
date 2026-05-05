package com.example.paceapp.core.components

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import com.example.paceapp.R
import com.example.paceapp.core.extensions.defaultClickable
import com.example.paceapp.theme.AppColors
import com.example.paceapp.theme.AppTheme
import com.wvelabs.core_ui.components.AppNetworkImage

// Adjust your imports as needed for AppColors, AppTheme, AppNetworkImage, R

@Composable
fun CustomProfileImage(
    modifier: Modifier = Modifier,
    imageUrl: String?,
    size: DpSize = DpSize(width = 100.dp, height = 108.dp),
    shape: Shape = RoundedCornerShape(
        topStart = 72.dp,
        topEnd = 72.dp,
        bottomEnd = 10.dp,
        bottomStart = 10.dp
    ),
    backgroundColor: Color = AppColors.White,
    isClickable: Boolean = true,
    showCameraIcon: Boolean = true, // Controls clickability and camera overlay
    showLabel: Boolean = showCameraIcon, // Defaults to true if editable, false if view-only
    onClick: () -> Unit = {},
    placeholder: Painter? = null // Custom placeholder
) {
    val hasProfileImage = !imageUrl.isNullOrBlank()

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        Box(
            modifier = Modifier
                .size(size)
                .clip(shape = shape)
                .background(color = backgroundColor)
                .then(
                    if (isClickable) Modifier.defaultClickable { onClick() }
                    else Modifier
                ),
            contentAlignment = Alignment.Center,
        ) {

            // Image or Placeholder
            AppNetworkImage(
                imageUrl = imageUrl,
                placeholder = placeholder,
                modifier = Modifier.fillMaxSize(),
            )
            // Camera Overlay
            // Only show camera if editable. If there is NO image but a placeholder IS provided,
            // hide the camera icon (per your requirements).
            if (isClickable && showCameraIcon) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            color = when {
                                hasProfileImage -> AppColors.Black.copy(alpha = 0.7f)
                                else -> AppColors.Transparent
                            }
                        ),
                    contentAlignment = Alignment.Center,
                ) {
                    Image(
                        painter = painterResource(R.drawable.ic_camera),
                        contentDescription = "Edit Photo",
                    )
                }
            }
        }

        // Label (Add / Update Photo)
        if (showLabel) {
            Crossfade(
                targetState = hasProfileImage,
                animationSpec = tween(300),
                modifier = Modifier.padding(top = 16.dp),
                label = "photo_label_crossfade"
            ) { hasImage ->
                Text(
                    text = when {
                        hasImage -> stringResource(R.string.update_photo)
                        else -> stringResource(R.string.add_photo)
                    },
                    style = AppTheme.typography.size16.copy(
                        fontWeight = FontWeight.Medium,
                        color = AppColors.White
                    )
                )
            }
        }
    }
}