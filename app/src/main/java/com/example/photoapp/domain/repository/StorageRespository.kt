package com.example.photoapp.domain.repository

import com.example.photoapp.data.model.Photo
import com.example.photoapp.util.NetworkResult
import kotlinx.coroutines.flow.Flow

interface StorageRepository {
    fun getPhotos(): Flow<NetworkResult<List<Photo>>>
}