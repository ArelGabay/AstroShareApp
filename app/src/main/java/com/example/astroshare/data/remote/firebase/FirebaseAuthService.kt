// data/remote/firebase/FirebaseAuthService.kt
package com.example.astroshare.data.remote.firebase

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.tasks.await

class FirebaseAuthService(private val firebaseAuth: FirebaseAuth) {

    suspend fun registerUser(email: String, password: String): FirebaseUser? {
        val result = firebaseAuth.createUserWithEmailAndPassword(email, password).await()
        return result.user
    }

    suspend fun loginUser(email: String, password: String): FirebaseUser? {
        val result = firebaseAuth.signInWithEmailAndPassword(email, password).await()
        return result.user
    }

    fun logout() {
        firebaseAuth.signOut()
    }

    fun currentUser(): FirebaseUser? = firebaseAuth.currentUser
}