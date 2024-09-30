package com.example.photoapp.ui.screens.feed

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import com.example.photoapp.R

@Composable
fun SortOptionsItem(onSortButtonClick: (SortType) -> Unit) {
    var expanded by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .wrapContentSize()
    ) {
        IconButton(
            onClick = { expanded = true },
        ) {
            Icon(
                painterResource(id = R.drawable.ic_sort),
                "sort",
                tint = MaterialTheme.colorScheme.primary,
            )
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
        ) {
            DropdownMenuItem(
                text = { Text("Name Low to High") },
                onClick = {
                    onSortButtonClick.invoke(SortType.NAME_LOW_TO_HIGH)
                    expanded = false
                }
            )
            DropdownMenuItem(
                text = { Text("Date Low to High") },
                onClick = {
                    onSortButtonClick.invoke(SortType.DATE_LOW_TO_HIGH)
                    expanded = false
                }
            )
            DropdownMenuItem(
                text = { Text("Name High to Low") },
                onClick = {
                    onSortButtonClick.invoke(SortType.NAME_HIGH_TO_LOW)
                    expanded = false
                }
            )
            DropdownMenuItem(
                text = { Text("Date High to Low") },
                onClick = {
                    onSortButtonClick.invoke(SortType.DATE_HIGH_TO_LOW)
                    expanded = false
                }
            )
        }
    }
}

@Composable
@Preview
fun PreviewSortOptionsItem() {
    SortOptionsItem {

    }
}