package com.example.photoapp.di

import com.example.photoapp.data.repository.StorageServiceImpl
import com.example.photoapp.domain.repository.StorageRepository
import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object FirebaseModule {
    @Provides
    fun firestore(): FirebaseFirestore = Firebase.firestore

    @Provides
    @Singleton
    fun providesStorageRepositoryImpl(firestore: FirebaseFirestore): StorageRepository {
        return StorageServiceImpl(firestore = firestore)
    }

}