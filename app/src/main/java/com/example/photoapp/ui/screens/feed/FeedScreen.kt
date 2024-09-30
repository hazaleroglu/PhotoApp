package com.example.photoapp.ui.screens.feed

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Environment
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.photoapp.R
import com.example.photoapp.data.model.Photo
import com.example.photoapp.ui.components.CustomFloatingActionButton
import com.example.photoapp.util.decodeBase64ToImage
import com.example.photoapp.util.encodeImageToBase64
import com.example.photoapp.util.getImageName
import com.example.photoapp.util.toBitmap
import java.io.File
import com.example.photoapp.R.string as AppText

@Composable
fun FeedScreen(viewModel: FeedViewModel = hiltViewModel()) {
    val photos by viewModel.photos.collectAsState()

    FeedScreenView(
        photos,
        viewModel::loadPhotoToStorage,
        viewModel::sortItems,
        viewModel::filterItemsByDate,
        viewModel::clearItemsFilter
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FeedScreenView(
    photos: List<Photo>,
    loadImageToStorage: (Photo, () -> Unit, (Exception) -> Unit) -> Unit,
    sortButtonClick: (SortType) -> Unit,
    filterButtonClicked: (Long, Long) -> Unit,
    clearFilterButtonClicked: () -> Unit
) {
    val context = LocalContext.current
    val contentResolver = context.contentResolver
    var selectedImage by remember { mutableStateOf<Uri?>(null) }
    var takenPhotoUri by remember { mutableStateOf<Uri?>(null) }
    var hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    val singlePhotoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri ->
            if (uri != null) {
                selectedImage = uri

                loadImageToStorage.invoke(
                    Photo(
                        name = selectedImage?.getImageName(contentResolver),
                        base64String = selectedImage?.toBitmap(contentResolver)
                            ?.encodeImageToBase64() ?: ""
                    ),
                    {
                        Toast.makeText(context, AppText.upload_successful, Toast.LENGTH_SHORT)
                            .show()
                    }, {
                        Toast.makeText(context, AppText.upload_unsuccessful, Toast.LENGTH_SHORT)
                            .show()
                    })
            }
        }
    )

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            loadImageToStorage.invoke(
                Photo(
                    name = takenPhotoUri?.getImageName(contentResolver),
                    base64String = takenPhotoUri?.toBitmap(contentResolver)
                        ?.encodeImageToBase64() ?: ""
                ),
                {
                    Toast.makeText(context, AppText.upload_successful, Toast.LENGTH_SHORT)
                        .show()
                }, {
                    Toast.makeText(context, AppText.upload_unsuccessful, Toast.LENGTH_SHORT)
                        .show()
                })
        }
    }

    val requestCameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        hasCameraPermission = isGranted
    }

    fun launchPhotoPicker() {
        singlePhotoPickerLauncher.launch(
            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
        )
    }

    fun galleryButtonClicked() {
        selectedImage = null
        launchPhotoPicker()
    }

    fun cameraButtonClicked() {
        if (hasCameraPermission) {
            val uri = createImageFile(context)
            takenPhotoUri = uri

            cameraLauncher.launch(uri)
        } else {
            requestCameraPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }


    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = stringResource(AppText.feed_title),
                            fontSize = (36.sp),
                            fontWeight = FontWeight.Black,
                            color = MaterialTheme.colorScheme.primary
                        )

                        Row(modifier = Modifier.fillMaxWidth()) {
                            SortOptionsItem(onSortButtonClick = { sortType ->
                                sortButtonClick.invoke(sortType)
                            })

                            DatePickerItem(dateSelected = { startDate, endDate ->
                                if (startDate != null && endDate != null) {
                                    filterButtonClicked.invoke(startDate, endDate)
                                }
                            })

                            IconButton(onClick = { clearFilterButtonClicked.invoke() }) {
                                Icon(
                                    painterResource(R.drawable.ic_filter_off), "filter off",
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                        }

                    }
                }
            )
        },
        floatingActionButton = {
            CustomFloatingActionButton({ galleryButtonClicked() }, { cameraButtonClicked() })
        },
        floatingActionButtonPosition = FabPosition.End
    ) { paddingValues ->
        PhotoGrid(modifier = Modifier.padding(paddingValues), photos = photos)
    }
}

@Composable
fun PhotoGrid(photos: List<Photo>, modifier: Modifier = Modifier) {
    val columns = 3

    LazyVerticalGrid(
        columns = GridCells.Fixed(columns),
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(8.dp)
    ) {
        items(photos) { photo ->
            PhotoItem(photo = photo)
        }
    }
}

@Composable
fun PhotoItem(photo: Photo) {
    val base64String = photo.base64String
    val bitmap = remember { base64String.decodeBase64ToImage() }

    bitmap?.let {
        val image: ImageBitmap = it.asImageBitmap()
        var isFullScreen by remember { mutableStateOf(false) }

        Image(
            bitmap = image,
            contentDescription = null,
            modifier = Modifier
                .padding(4.dp)
                .aspectRatio(1f)
                .fillMaxWidth()
                .clickable {
                    isFullScreen = true
                },
            contentScale = ContentScale.Crop
        )

        if (isFullScreen) {
            FullScreenImageDialog(image = image, onDismiss = { isFullScreen = false })
        }
    }
}


@Composable
fun FullScreenImageDialog(image: ImageBitmap, onDismiss: () -> Unit) {
    Dialog(onDismissRequest = { onDismiss() }) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Image(
                bitmap = image,
                contentDescription = "Image",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Fit
            )

            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.TopEnd) {
                IconButton(
                    onClick = { onDismiss() },
                    modifier = Modifier
                        .padding(16.dp)
                        .size(48.dp)
                        .align(Alignment.TopEnd)
                ) {
                    Icon(
                        Icons.Filled.Close,
                        contentDescription = "Close",
                        tint = Color.White
                    )
                }
            }
        }
    }
}

fun createImageFile(context: Context): Uri {
    // Create a file to store the image
    val photoFile = File(
        context.getExternalFilesDir(Environment.DIRECTORY_PICTURES),
        "photo_${System.currentTimeMillis()}.jpg"
    )
    return FileProvider.getUriForFile(
        context,
        "${context.packageName}.provider",
        photoFile
    )
}

@Preview
@Composable
fun FeedScreenPreview() {
    val list = mutableListOf<Photo>()
    list.add(
        Photo(
            "1",
            null,
            "photo",
            "https://media.istockphoto.com/id/1458782106/tr/foto%C4%9Fraf/scenic-aerial-view-of-the-mountain-landscape-with-a-forest-and-the-crystal-blue-river-in.jpg?s=1024x1024&w=is&k=20&c=23AxF-teg7zZCmX2IheKtvG9h3hKCA0M7q3UZNOHuAA="
        )
    )
    list.add(
        Photo(
            "2",
            null,
            "photo2",
            "https://media.istockphoto.com/id/1458782106/tr/foto%C4%9Fraf/scenic-aerial-view-of-the-mountain-landscape-with-a-forest-and-the-crystal-blue-river-in.jpg?s=1024x1024&w=is&k=20&c=23AxF-teg7zZCmX2IheKtvG9h3hKCA0M7q3UZNOHuAA="
        )
    )

    //FeedScreenView(list)

}