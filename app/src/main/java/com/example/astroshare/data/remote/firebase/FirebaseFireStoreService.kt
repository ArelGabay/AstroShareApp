// data/remote/firebase/FirebaseFirestoreService.kt
package com.example.astroshare.data.remote.firebase

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class FirebaseFirestoreService(private val firestore: FirebaseFirestore) {

    // Example function: Save a user document
    suspend fun saveUser(userId: String, data: Map<String, Any>) {
        firestore.collection("users").document(userId)
            .set(data)
            .await()
    }

    // Example function: Get user document
    suspend fun getUser(userId: String): Map<String, Any>? {
        val snapshot = firestore.collection("users").document(userId)
            .get()
            .await()
        return snapshot.data
    }
}