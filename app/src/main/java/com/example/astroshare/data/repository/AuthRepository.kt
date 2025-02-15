package com.example.astroshare.data.repository

import com.example.astroshare.data.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class AuthRepository {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()

    /**
     * Registers a new user and returns the created User object.
     */
    fun registerUser(email: String, password: String, displayName: String, onComplete: (Boolean, String?) -> Unit) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val firebaseUser = auth.currentUser
                    if (firebaseUser != null) {
                        val user = User(
                            id = firebaseUser.uid,
                            email = email,
                            displayName = displayName,
                            profilePicture = null,
                            bio = null,
                            postsCount = 0,
                            lastLogin = System.currentTimeMillis(),
                            isLoggedIn = true
                        )

                        // Save user to Firestore
                        firestore.collection("users").document(firebaseUser.uid).set(user)
                            .addOnSuccessListener {
                                onComplete(true, null) // Success
                            }
                            .addOnFailureListener { e ->
                                onComplete(false, e.message) // Firestore save failed
                            }
                    } else {
                        onComplete(false, "User not found after registration") // No user
                    }
                } else {
                    onComplete(false, task.exception?.message) // Firebase Auth error
                }
            }
    }

    /**
     * Logs in an existing user and retrieves user data.
     */
    fun loginUser(email: String, password: String, onComplete: (User?, String?) -> Unit) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val firebaseUser = auth.currentUser
                    if (firebaseUser != null) {
                        // Fetch user data from Firestore
                        firestore.collection("users").document(firebaseUser.uid).get()
                            .addOnSuccessListener { document ->
                                if (document.exists()) {
                                    val user = document.toObject(User::class.java)
                                    onComplete(user, null) // Return user object
                                } else {
                                    onComplete(null, "User data not found in Firestore")
                                }
                            }
                            .addOnFailureListener { e ->
                                onComplete(null, e.message) // Firestore error
                            }
                    } else {
                        onComplete(null, "Firebase user is null")
                    }
                } else {
                    onComplete(null, task.exception?.message) // Firebase Auth error
                }
            }
    }

    /**
     * Logs out the user.
     */
    fun logout() {
        auth.signOut()
    }

    /**
     * Gets the currently authenticated user.
     */
    fun getCurrentUserId(): String? {
        return auth.currentUser?.uid
    }

    fun getUserData(onComplete: (User?, String?) -> Unit) {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            firestore.collection("users").document(currentUser.uid).get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        val user = document.toObject(User::class.java)
                        onComplete(user, null) // Return user object
                    } else {
                        onComplete(null, "User data not found in Firestore")
                    }
                }
                .addOnFailureListener { e ->
                    onComplete(null, e.message) // Firestore error
                }
        } else {
            onComplete(null, "User is not logged in")
        }
    }

}