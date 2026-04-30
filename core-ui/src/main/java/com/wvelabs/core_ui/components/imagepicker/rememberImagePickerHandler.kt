package com.wvelabs.core_ui.components.imagepicker

import android.app.Activity
import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.FileProvider
import com.wvelabs.core_ui.extensions.createTempPictureUri
import com.yalantis.ucrop.UCrop
import java.io.File

interface ImagePickerHandler {
    fun openGallery()
    fun openCamera()
}
@Composable
fun rememberImagePickerHandler(
    shouldCrop: Boolean = true,
    onResult: (ImagePickerAction) -> Unit
): ImagePickerHandler {
    val context = LocalContext.current

    // 1. uCrop Launcher (The Alternative to CanHub)
    val uCropLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK && result.data != null) {
            val resultUri = UCrop.getOutput(result.data!!)
            if (resultUri != null) {
                onResult(ImagePickerAction.Selected(resultUri))
            } else {
                onResult(ImagePickerAction.Cancelled)
            }
        } else {
            onResult(ImagePickerAction.Cancelled)
        }
    }

    // Helper to start uCrop Activity
    val startCrop = { sourceUri: Uri ->
        // uCrop needs a destination file to save the cropped image
        val destinationUri = Uri.fromFile(
            File(context.cacheDir, "cropped_${System.currentTimeMillis()}.jpg")
        )

        val intent = UCrop.of(sourceUri, destinationUri)
            .withAspectRatio(1f, 1f) // Square crop for profiles
            .withMaxResultSize(1080, 1080)
            .getIntent(context)

        uCropLauncher.launch(intent)
    }

    // 2. Gallery Launcher
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            if (shouldCrop) startCrop(uri) else onResult(ImagePickerAction.Selected(uri))
        } else {
            onResult(ImagePickerAction.Cancelled)
        }
    }

    // 3. Camera Launcher
    var tempUri by remember { mutableStateOf<Uri?>(null) }
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success && tempUri != null) {
            if (shouldCrop) startCrop(tempUri!!) else onResult(ImagePickerAction.Selected(tempUri!!))
        } else {
            onResult(ImagePickerAction.Cancelled)
        }
    }

    return remember {
        object : ImagePickerHandler {
            override fun openGallery() = galleryLauncher.launch(
                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
            )

            override fun openCamera() {
                val uri = context.createTempPictureUri()
                tempUri = uri
                cameraLauncher.launch(uri)
            }
        }
    }
}

