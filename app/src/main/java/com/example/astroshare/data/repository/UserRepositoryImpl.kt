package com.example.astroshare.data.repository

import android.net.Uri
import android.util.Log
import com.example.astroshare.data.local.UserLocalDataSource
import com.example.astroshare.data.model.User
import com.example.astroshare.data.remote.firebase.FirebaseAuthService
import com.example.astroshare.data.remote.firebase.FirebaseFirestoreService
import com.example.astroshare.data.remote.firebase.FirebaseStorageService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class UserRepositoryImpl(
    private val localDataSource: UserLocalDataSource,
    private val authService: FirebaseAuthService,
    private val firestoreService: FirebaseFirestoreService,
    private val storageService: FirebaseStorageService
) : UserRepository {

    override suspend fun registerWithImage(email: String, password: String, imageUri: Uri): Result<User> =
        withContext(Dispatchers.IO) {
            try {
                // 1. Register with Firebase Auth
                val firebaseUser = authService.registerUser(email, password)
                    ?: throw Exception("Registration failed")

                val userId = firebaseUser.uid

                // 2. Upload profile image to Firebase Storage
                val imageUrl = storageService.uploadUserImage(userId, imageUri)

                // 3. Create User model instance
                val newUser = User(
                    id = userId,
                    displayName = firebaseUser.displayName ?: "",
                    email = firebaseUser.email ?: email,
                    bio = "", // Default empty bio
                    lastLogin = System.currentTimeMillis(),
                    loggedIn = true,
                    postsCount = 0,
                    profilePicture = imageUrl
                )

                // 4. Create Firestore user map
                val userMap = mapOf(
                    "displayName" to newUser.displayName,
                    "email" to newUser.email,
                    "bio" to (newUser.bio ?: ""),
                    "lastLogin" to newUser.lastLogin,
                    "loggedIn" to newUser.loggedIn,
                    "postsCount" to newUser.postsCount,
                    "profilePicture" to (newUser.profilePicture ?: "")
                )

                // 5. Save to Firestore
                firestoreService.saveUser(userId, userMap)

                // 6. Save locally in Room
                localDataSource.insertUser(newUser)

                Log.d("RoomTest", "Inserted user with ID: ${newUser.id}")

                Result.success(newUser)
            } catch (e: Exception) {
                Log.e("UserRepository", "Error in registerWithImage", e)
                Result.failure(e)
            }
        }

    override suspend fun login(email: String, password: String): Result<User> =
        withContext(Dispatchers.IO) {
            try {
                // 1. Login via Firebase Auth
                val firebaseUser = authService.loginUser(email, password)
                    ?: throw Exception("Login failed")

                val userId = firebaseUser.uid

                // 2. Try to get from local cache (Room)
                var user = localDataSource.getUserById(userId)

                // 3. If not found locally, fetch from Firestore
                if (user == null) {
                    val userData = firestoreService.getUser(userId)
                    if (userData != null) {
                        user = User(
                            id = userId,
                            displayName = userData["displayName"] as? String ?: "",
                            email = userData["email"] as? String ?: firebaseUser.email ?: email,
                            bio = userData["bio"] as? String,
                            lastLogin = userData["lastLogin"] as? Long ?: System.currentTimeMillis(),
                            loggedIn = userData["loggedIn"] as? Boolean ?: true,
                            postsCount = (userData["postsCount"] as? Long)?.toInt() ?: 0,
                            profilePicture = userData["profilePicture"] as? String
                        )

                        // Cache the user locally
                        localDataSource.insertUser(user)
                    } else {
                        throw Exception("User not found in Firestore")
                    }
                }

                Result.success(user)
            } catch (e: Exception) {
                Log.e("UserRepository", "Error in login", e)
                Result.failure(e)
            }
        }

    override suspend fun updateUser(user: User): Result<Unit> =
        withContext(Dispatchers.IO) {
            try {
                val userMap = mapOf(
                    "displayName" to user.displayName,
                    "email" to user.email,
                    "bio" to (user.bio ?: ""),
                    "lastLogin" to user.lastLogin,
                    "loggedIn" to user.loggedIn,
                    "postsCount" to user.postsCount,
                    "profilePicture" to (user.profilePicture ?: "")
                )

                // Update Firestore
                firestoreService.saveUser(user.id, userMap)

                // Update Room local cache
                localDataSource.updateUser(user)

                Result.success(Unit)
            } catch (e: Exception) {
                Log.e("UserRepository", "Error in updateUser", e)
                Result.failure(e)
            }
        }

    override suspend fun deleteUser(userId: String): Result<Unit> =
        withContext(Dispatchers.IO) {
            try {
                // Delete locally (optional: delete from Firestore if needed)
                val user = localDataSource.getUserById(userId)
                if (user != null) {
                    localDataSource.deleteUser(user)
                }

                Result.success(Unit)
            } catch (e: Exception) {
                Log.e("UserRepository", "Error in deleteUser", e)
                Result.failure(e)
            }
        }

    override suspend fun getUser(userId: String): User? =
        withContext(Dispatchers.IO) {
            localDataSource.getUserById(userId)
        }
}