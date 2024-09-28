package com.example.photoapp.ui.screens.feed

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.BottomAppBarDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LargeFloatingActionButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.photoapp.data.model.Photo
import com.example.photoapp.util.decodeBase64ToImage
import com.example.photoapp.util.encodeImageToBase64
import com.example.photoapp.util.getImageName
import com.example.photoapp.util.toBitmap
import com.example.photoapp.R.string as AppText

@Composable
fun FeedScreen(viewModel: FeedViewModel = hiltViewModel()) {
    val photos by viewModel.photos.collectAsState()

    FeedScreenView(photos, viewModel::loadPhotoToStorage)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FeedScreenView(
    photos: List<Photo>,
    loadImageToStorage: (Photo, () -> Unit, (Exception) -> Unit) -> Unit
) {
    val context = LocalContext.current
    val contentResolver = context.contentResolver
    var selectedImage by remember {
        mutableStateOf<Uri?>(null)
    }

    val singlePhotoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri -> selectedImage = uri }
    )

    fun launchPhotoPicker() {
        singlePhotoPickerLauncher.launch(
            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
        )
        loadImageToStorage.invoke(
            Photo(
                name = selectedImage?.getImageName(contentResolver),
                base64String = selectedImage?.toBitmap(contentResolver)?.encodeImageToBase64() ?: ""
            ),
            {
                Toast.makeText(context, AppText.upload_successful, Toast.LENGTH_SHORT).show()
            }, {
                Toast.makeText(context, AppText.upload_unsuccessful, Toast.LENGTH_SHORT).show()
            })
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(text = stringResource(AppText.feed_title)) }
            )
        },
        floatingActionButton = {
            LargeFloatingActionButton(
                onClick = { launchPhotoPicker() },
                containerColor = BottomAppBarDefaults.bottomAppBarFabColor,
                elevation = FloatingActionButtonDefaults.bottomAppBarFabElevation(),
            ) {
                Icon(Icons.Filled.Add, "Add")
            }
        },
        floatingActionButtonPosition = FabPosition.Center
    ) { paddingValues ->
        PhotoGrid(photos = photos, Modifier.padding(paddingValues))
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
        Image(
            bitmap = it.asImageBitmap(),
            contentDescription = null,
            modifier = Modifier
                .padding(4.dp)
                .aspectRatio(1f)
                .fillMaxWidth(),
            contentScale = ContentScale.Crop
        )
    }
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