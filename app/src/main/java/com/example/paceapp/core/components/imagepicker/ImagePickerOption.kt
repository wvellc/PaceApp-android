package com.example.paceapp.core.components.imagepicker

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import com.example.paceapp.R
import com.example.paceapp.core.components.AppTextButton
import com.example.paceapp.theme.AppColors
import com.example.paceapp.theme.AppTheme
import com.wvelabs.core_ui.components.imagepicker.PickerOptionType

val PickerOptionType.title
    @Composable
    get() = when (this) {
        PickerOptionType.CAMERA -> stringResource(R.string.camera)
        PickerOptionType.GALLERY -> stringResource(R.string.photo_gallery)
        PickerOptionType.REPLACE -> stringResource(R.string.replace)
        PickerOptionType.REMOVE -> stringResource(R.string.remove)
    }
val PickerOptionType.tint
    get() = when (this) {
        PickerOptionType.REMOVE -> AppColors.Error
        else -> AppColors.NeonAquaBlue
    }


@Composable
fun ImagePickerOption(
    type: PickerOptionType,
    onClick: () -> Unit
) {
    AppTextButton(
        onClick = onClick,
        text = type.name,
        contentColor = type.tint,
        style = AppTheme.typography.size16.copy(
            fontWeight = FontWeight.Medium,
        ),
        modifier = Modifier
            .fillMaxWidth()
    )
}