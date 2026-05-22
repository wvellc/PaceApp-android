package net.paceapp.core.components.imagepicker

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import net.paceapp.R
import net.paceapp.core.components.AppTextButton
import net.paceapp.theme.AppColors
import net.paceapp.theme.AppTheme
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
        text = type.title.uppercase(),
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth(),
        contentColor = type.tint,
        style = AppTheme.typography.medium.copy(
            fontSize = 16.sp,
            letterSpacing = 1.sp
        ),
    )
}