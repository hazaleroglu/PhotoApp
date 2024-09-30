package com.example.photoapp.util

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.core.content.ContextCompat
import com.example.photoapp.data.model.Photo
import com.example.photoapp.ui.screens.feed.createImageFile

@Composable
fun cameraPermission(context: Context, loadImage: (Photo) -> Unit) {
    var takenPhotoUri by remember { mutableStateOf<Uri?>(null) }
    val contentResolver = context.contentResolver
    val cameraLauncher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.TakePicture()
        ) { success ->
            if (success) {
                loadImage.invoke(
                    Photo(
                        name = takenPhotoUri?.getImageName(contentResolver),
                        base64String = takenPhotoUri?.toBitmap(contentResolver)
                            ?.encodeImageToBase64() ?: ""
                    )
                )
            }
        }

    var hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    if (hasCameraPermission) {
        val uri = createImageFile(context)
        takenPhotoUri = uri
        SideEffect {
            cameraLauncher.launch(uri)
        }
    } else {
        val requestCameraPermissionLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            hasCameraPermission = isGranted
            if (isGranted) {
                val uri = createImageFile(context)
                takenPhotoUri = uri
            }
        }
        requestCameraPermissionLauncher.launch(Manifest.permission.CAMERA)
    }
}

@Composable
fun photoPicker(context: Context, loadImage: (Photo) -> Unit) {
    var selectedImage by remember { mutableStateOf<Uri?>(null) }
    val contentResolver = context.contentResolver
    val singlePhotoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri ->
            if (uri != null) {
                selectedImage = uri
                loadImage.invoke(
                    Photo(
                        name = selectedImage?.getImageName(contentResolver),
                        base64String = selectedImage?.toBitmap(contentResolver)
                            ?.encodeImageToBase64() ?: ""
                    )
                )
            }
        }
    )

    selectedImage = null
    SideEffect {
        singlePhotoPickerLauncher.launch(
            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
        )
    }
}


