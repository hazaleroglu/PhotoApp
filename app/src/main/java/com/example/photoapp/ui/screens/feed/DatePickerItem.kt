package com.example.photoapp.ui.screens.feed

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
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
import com.example.photoapp.ui.components.DatePickerDialog

@Composable
fun DatePickerItem(dateSelected: (Long?, Long?) -> Unit) {
    var filterExpanded by remember { mutableStateOf(false) }
    var datePickerState by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier.wrapContentSize()
    ) {
        IconButton(
            onClick = { filterExpanded = true },
        ) {
            Icon(
                painterResource(id = R.drawable.ic_filter),
                "filter",
                tint = MaterialTheme.colorScheme.primary,
            )
        }

        DropdownMenu(
            expanded = filterExpanded,
            onDismissRequest = { filterExpanded = false },
        ) {
            DropdownMenuItem(
                text = { Text("Select Date Range") },
                onClick = {
                    datePickerState = true
                }
            )
        }
        if (datePickerState) {
            Row {
                DatePickerDialog(onDateRangeSelected = { startDate, endDate ->
                    dateSelected.invoke(
                        startDate,
                        endDate
                    )
                }) {
                    datePickerState = false
                    filterExpanded = false
                }
            }
        }

    }
}

@Preview
@Composable
fun PreviewDatePickerItem() {
    DatePickerItem { _, _ -> }
}