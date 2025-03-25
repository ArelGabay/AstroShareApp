package com.example.astroshare.data.repository

import android.content.Context
import android.net.Uri
import android.util.Log
import com.example.astroshare.data.local.UserLocalDataSource
import com.example.astroshare.data.model.User
import com.example.astroshare.data.remote.firebase.FirebaseAuthService
import com.example.astroshare.data.remote.firebase.FirebaseFirestoreService
import com.example.astroshare.data.remote.firebase.CloudinaryStorageService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class UserRepositoryImpl(
    private val localDataSource: UserLocalDataSource,
    private val authService: FirebaseAuthService,
    private val firestoreService: FirebaseFirestoreService,
    private val cloudinaryService: CloudinaryStorageService,
    private val appContext: Context
) : UserRepository {

    override suspend fun registerWithImage(
        email: String,
        password: String,
        displayName: String,
        imageUri: Uri
    ): Result<User> {
        return withContext(Dispatchers.IO) {
            try {
                val firebaseUser = authService.registerUser(email, password)
                    ?: throw Exception("Registration failed")

                val userId = firebaseUser.uid

                // Upload the image via Cloudinary
                val imageUrl = cloudinaryService.uploadUserImage(userId, imageUri, appContext)

                val newUser = User(
                    id = userId,
                    displayName = displayName,
                    email = email,
                    bio = "",
                    lastLogin = System.currentTimeMillis(),
                    loggedIn = true,
                    postsCount = 0,
                    profilePicture = imageUrl
                )

                val userMap: Map<String, Any> = mapOf(
                    "displayName" to newUser.displayName,
                    "email" to newUser.email,
                    "bio" to (newUser.bio ?: ""),
                    "lastLogin" to newUser.lastLogin,
                    "loggedIn" to newUser.loggedIn,
                    "postsCount" to newUser.postsCount,
                    "profilePicture" to (newUser.profilePicture ?: "")
                )

                firestoreService.saveUser(userId, userMap)
                localDataSource.insertUser(newUser)

                Result.success(newUser)

            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    override suspend fun login(email: String, password: String): Result<User> =
        withContext(Dispatchers.IO) {
            try {
                val firebaseUser = authService.loginUser(email, password)
                    ?: throw Exception("Login failed")
                val userId = firebaseUser.uid

                var user = localDataSource.getUserById(userId)
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
                firestoreService.saveUser(user.id, userMap)
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