package com.wvelabs.core_ui.components.imagepicker

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BaseImagePicker(
    isVisible: Boolean,
    hasExistingImage: Boolean,
    cropEnabled: Boolean = true,
    backgroundColor: Color = Color.White,
    overlayColor: Color = Color.Black.copy(alpha = 0.2f),
    dragHandleColor: Color = Color.Gray,
    sheetShape: Shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
    onDismiss: () -> Unit,
    onAction: (ImagePickerAction) -> Unit,
    optionItem: @Composable (type: PickerOptionType, onClick: () -> Unit) -> Unit,
    cancelButton: @Composable (onClick: () -> Unit) -> Unit
) {
    val handler = rememberImagePickerHandler(cropEnabled, onAction)
    val scope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var activeSheet by remember(isVisible) {
        mutableStateOf(if (hasExistingImage) ImagePickerSheetType.EDIT else ImagePickerSheetType.SOURCE)
    }
    var isAnimating by remember { mutableStateOf(false) }
    fun dismissSheet() {
        if (!isAnimating) {
            isAnimating = true
            scope.launch {
                sheetState.hide()
                onDismiss()
                isAnimating = false
            }
        }
    }

    if (isVisible) {
        ModalBottomSheet(
            onDismissRequest = onDismiss,
            sheetState = sheetState,
            containerColor = backgroundColor,
            shape = sheetShape,
            scrimColor = overlayColor,
            dragHandle = {
                BottomSheetDefaults.DragHandle(
                    color = dragHandleColor
                )
            }
        ) {
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                when (activeSheet) {
                    ImagePickerSheetType.SOURCE -> {
                        optionItem(PickerOptionType.CAMERA) {
                            handler.openCamera()
                            dismissSheet()
                        }
                        optionItem(PickerOptionType.GALLERY) {
                            handler.openGallery()
                            dismissSheet()
                        }
                    }

                    ImagePickerSheetType.EDIT -> {
                        optionItem(PickerOptionType.REPLACE) {
                            if (!isAnimating) {
                                isAnimating = true
                                scope.launch {
                                    // 1. Animate down
                                    sheetState.hide()
                                    // 2. Swap content while hidden
                                    activeSheet = ImagePickerSheetType.SOURCE
                                    // 3. Animate back up
                                    sheetState.show()
                                    isAnimating = false
                                }
                            }
                        }
                        optionItem(PickerOptionType.REMOVE) {
                            onAction(ImagePickerAction.Removed)
                            dismissSheet()
                        }
                    }

                    else -> {}
                }

                Spacer(modifier = Modifier.height(16.dp))

                // The Dynamic Cancel Button
                cancelButton {
                    dismissSheet()
                }

                Spacer(modifier = Modifier.height(16.dp)) // Safe area padding}
            }
        }
    }
}

