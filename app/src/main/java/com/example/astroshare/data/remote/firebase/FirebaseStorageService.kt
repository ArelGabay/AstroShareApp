// data/remote/firebase/FirebaseStorageService.kt
package com.example.astroshare.data.remote.firebase

import android.net.Uri
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await

class FirebaseStorageService(private val storage: FirebaseStorage) {

    suspend fun uploadUserImage(userId: String, imageUri: Uri): String {
        val storageRef = storage.reference
            .child("users")
            .child(userId)
            .child("profile.jpg")

        storageRef.putFile(imageUri).await()
        return storageRef.downloadUrl.await().toString()
    }
}