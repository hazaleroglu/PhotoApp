package com.example.photoapp.data.model

import com.google.firebase.Timestamp
import java.util.Date

data class Photo(
    val id: String = "",
    val addedDate: Timestamp? = null,
    val name: String? = "",
    val base64String: String = ""
) {
    fun toDate(): Date? {
        return addedDate?.toDate()
    }
}