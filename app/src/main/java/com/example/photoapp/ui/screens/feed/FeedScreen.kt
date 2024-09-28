package com.example.photoapp.ui.screens.feed

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import coil.compose.rememberImagePainter
import com.example.photoapp.data.model.Photo

@Composable
fun FeedScreen(viewModel: FeedViewModel = hiltViewModel()) {
    val photos by viewModel.photos.collectAsState()

    FeedScreenView(photos)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FeedScreenView(photos: List<Photo>) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Fotoğraflar") }
            )
        },
    ) { paddingValues ->
        LazyColumn(contentPadding = paddingValues) {
            items(photos) { photo ->
                PhotoItem(photo = photo)
            }
        }
    }
}

@Composable
fun PhotoItem(photo: Photo) {
    AsyncImage(
        model = photo.photo,
        contentDescription = null,
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    )
}

@Preview
@Composable
fun FeedScreenPreview() {
    FeedScreen()
}