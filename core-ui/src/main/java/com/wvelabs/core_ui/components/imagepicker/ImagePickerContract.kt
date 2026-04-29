package com.wvelabs.core_ui.components.imagepicker

import android.net.Uri


sealed interface ImagePickerAction {
    data class Selected(val uri: Uri) : ImagePickerAction
    data class Cropped(val uri: Uri) : ImagePickerAction // New action for cropped results
    object Removed : ImagePickerAction
    object Cancelled : ImagePickerAction
}

enum class ImagePickerSheetType {
    NONE, SOURCE, EDIT
}
enum class PickerOptionType {
    CAMERA, GALLERY, REPLACE, REMOVE
}