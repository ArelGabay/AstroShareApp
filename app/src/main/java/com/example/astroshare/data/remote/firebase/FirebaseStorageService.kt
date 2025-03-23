package com.example.astroshare.data.remote.firebase

import android.net.Uri
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await

class FirebaseStorageService(private val storage: FirebaseStorage) {

    suspend fun uploadUserImage(userId: String, imageUri: Uri): String {
        val storageRef = storage.reference
            .child("profileImages")   // folder name
            .child("$userId.jpg")     // file name inside the folder

        storageRef.putFile(imageUri).await()

        // Return the download URL
        return storageRef.downloadUrl.await().toString()
    }
}