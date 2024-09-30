package com.example.photoapp.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.LargeFloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.photoapp.R

@Composable
fun CustomFloatingActionButton(
    galleryAction: @Composable () -> Unit,
    cameraAction: @Composable () -> Unit
) {
    var showMenu by remember { mutableStateOf(false) }
    var launchCamera by remember { mutableStateOf(false) }
    var photoPicker by remember { mutableStateOf(false) }

    if (launchCamera) {
        cameraAction.invoke()
        launchCamera = false
    }

    if (photoPicker) {
        galleryAction.invoke()
        photoPicker = false
    }

    Box(
        modifier = Modifier
            .wrapContentSize()
            .padding(16.dp)
    ) {

        LargeFloatingActionButton(
            modifier = Modifier.padding(top = 150.dp),
            onClick = { showMenu = !showMenu },
            containerColor = MaterialTheme.colorScheme.primary
        ) {
            Icon(Icons.Filled.Add, "add")
        }

        if (showMenu) {
            Column(
                modifier = Modifier
                    .wrapContentSize()
                    .padding(start = 24.dp)
            ) {
                FloatingActionButton(
                    onClick = {
                        showMenu = false
                        launchCamera = true
                    },
                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                ) {
                    Icon(
                        painterResource(id = R.drawable.ic_camera),
                        "camera",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }

                Spacer(modifier = Modifier.size(16.dp))

                FloatingActionButton(
                    onClick = {
                        showMenu = false
                        photoPicker = true
                    },
                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                ) {
                    Icon(
                        painterResource(id = R.drawable.ic_gallery),
                        "camera",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}

@Preview
@Composable
fun PreviewCustomFloatingActionButton() {
    //CustomFloatingActionButton()
}