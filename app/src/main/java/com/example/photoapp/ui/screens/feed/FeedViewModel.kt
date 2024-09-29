package com.example.photoapp.ui.screens.feed

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.photoapp.data.model.Photo
import com.example.photoapp.domain.repository.StorageRepository
import com.example.photoapp.util.NetworkResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FeedViewModel @Inject constructor(
    private val storageRepository: StorageRepository,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val _photos = MutableStateFlow<List<Photo>>(value = emptyList())
    val photos: StateFlow<List<Photo>> = _photos

    init {
        getPhotos()
    }

    private fun getPhotos() {
        viewModelScope.launch {
            storageRepository.getPhotos().collect { result ->
                when (result) {
                    is NetworkResult.Success -> {
                        _photos.value = result.data
                    }

                    is NetworkResult.Error -> {
                        //TODO:
                    }

                    is NetworkResult.Exception -> {
                        //TODO:
                    }
                }
            }
        }
    }

    fun loadPhotoToStorage(photo: Photo, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        storageRepository.uploadPhotoToFirestore(
            photo = photo,
            onSuccess = onSuccess,
            onFailure = onFailure

        )
    }

    fun sortItems(sortType: SortType) {
        _photos.update { photos ->
            when(sortType) {
                SortType.NAME -> photos.sortedBy { it.name }
                SortType.ADDED_DATE -> {
                    photos.sortedBy { it.addedDate }.reversed()
                }
            }
        }
    }
}