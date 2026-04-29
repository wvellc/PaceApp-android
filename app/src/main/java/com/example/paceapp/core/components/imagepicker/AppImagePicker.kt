package com.example.paceapp.core.components.imagepicker

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.paceapp.R
import com.example.paceapp.core.components.AppButton
import com.example.paceapp.theme.AppColors
import com.wvelabs.core_ui.components.imagepicker.BaseImagePicker
import com.wvelabs.core_ui.components.imagepicker.ImagePickerAction

@Composable
fun AppImagePicker(
    isVisible: Boolean = false,
    onDismiss: () -> Unit = { },
    showReplaceSheet: Boolean = false,
    onAction: (ImagePickerAction) -> Unit,
) {
    BaseImagePicker(
        isVisible = isVisible,
        hasExistingImage = showReplaceSheet,
        backgroundColor = AppColors.White,
        onDismiss = onDismiss,
        onAction = onAction,
        optionItem = { type, onClick -> ImagePickerOption(type, onClick) },
        cancelButton = { onCancelClick ->
            AppButton(
                modifier = Modifier.fillMaxWidth(),
                height = 46.dp,
                title = stringResource(R.string.cancel),
                onClick = onCancelClick
            )
        }
    )
}