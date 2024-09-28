package com.example.photoapp.data.repository

import com.example.photoapp.data.model.Photo
import com.example.photoapp.domain.repository.StorageRepository
import com.example.photoapp.util.NetworkResult
import com.google.firebase.firestore.FieldValue
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

    override fun uploadPhotoToFirestore(
        photo: Photo,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val photoData = hashMapOf(
            ADDED_DATE to FieldValue.serverTimestamp(),
            BASE64_STRING to photo.base64String,
            NAME to photo.name
        )

        firestore.collection(COLLECTION_NAME)
            .add(photoData)
            .addOnSuccessListener {
                onSuccess()
            }
            .addOnFailureListener { exception ->
                onFailure(exception)
            }
    }

    companion object {
        const val COLLECTION_NAME = "photos"
        const val NAME = "name"
        const val ADDED_DATE = "addedDate"
        const val BASE64_STRING = "base64String"
    }
}