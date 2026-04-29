package com.wvelabs.core_ui.components.imagepicker

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BaseImagePicker(
    isVisible: Boolean,
    hasExistingImage: Boolean,
    cropEnabled: Boolean = true,
    backgroundColor: Color = Color.White,
    sheetShape: Shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
    onDismiss: () -> Unit,
    onAction: (ImagePickerAction) -> Unit,
    optionItem: @Composable (type: PickerOptionType, onClick: () -> Unit) -> Unit,
    // NEW: Dedicated slot for the cancel button
    cancelButton: @Composable (onClick: () -> Unit) -> Unit
) {
    val handler = rememberImagePickerHandler(cropEnabled, onAction)
    val sheetState = rememberModalBottomSheetState()

    // Determine which "Logic" to show based on if an image exists
    var activeSheet by remember(isVisible) {
        mutableStateOf(if (hasExistingImage) ImagePickerSheetType.EDIT else ImagePickerSheetType.SOURCE)
    }

    if (isVisible) {
        ModalBottomSheet(
            modifier = Modifier.padding(16.dp),
            onDismissRequest = onDismiss,
            sheetState = sheetState,
            containerColor = backgroundColor,
            shape = sheetShape
        ) {
            when (activeSheet) {
                ImagePickerSheetType.SOURCE -> {
                    optionItem(PickerOptionType.CAMERA) {
                        handler.openCamera()
                        onDismiss()
                    }
                    optionItem(PickerOptionType.GALLERY) {
                        handler.openGallery()
                        onDismiss()
                    }
                }

                ImagePickerSheetType.EDIT -> {
                    optionItem(PickerOptionType.REPLACE) {
                        activeSheet = ImagePickerSheetType.SOURCE
                    }
                    optionItem(PickerOptionType.REMOVE) {
                        onAction(ImagePickerAction.Removed)
                        onDismiss()
                    }
                }

                else -> {}
            }

            Spacer(modifier = Modifier.height(16.dp))

            // The Dynamic Cancel Button
            cancelButton { onDismiss() }

            Spacer(modifier = Modifier.height(16.dp)) // Safe area padding
        }
    }
}

