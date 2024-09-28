package com.example.photoapp.data.repository

import com.example.photoapp.data.model.Photo
import com.example.photoapp.domain.repository.StorageRepository
import com.example.photoapp.util.NetworkResult
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject

class StorageServiceImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
) : StorageRepository {

    override fun getPhotos(): Flow<NetworkResult<List<Photo>>> = callbackFlow {
        val listenerRegistration = firestore.collection(COLLECTION_NAME)
            .addSnapshotListener { snapshots, e ->
                if (e != null) {
                    close(e)
                    return@addSnapshotListener
                }
                val photos = snapshots?.documents?.map { document ->
                    document.toObject(Photo::class.java)!!.copy(id = document.id)
                } ?: emptyList()

                trySend(NetworkResult.Success(photos)).isSuccess
            }
        awaitClose { listenerRegistration.remove() }
    }

    companion object {
        const val COLLECTION_NAME = "photos"
        const val NAME = "name"
        const val ID = "id"
        const val ADDED_DATE = "addedDate"
        const val PHOTO = "photo"
    }

//    fun DocumentSnapshot.toPhotoObject(): Photo {
//        return Photo(
//            this.getDate(ADDED_DATE),
//            this.getString(ID),
//            this.getString(NAME),
//            this.getString(PHOTO)
//        )
//    }
}