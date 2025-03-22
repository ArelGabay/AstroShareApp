// data/repository/UserRepository.kt
package com.example.astroshare.data.repository

import android.net.Uri
import com.example.astroshare.data.model.User

interface UserRepository {
    suspend fun registerWithImage(email: String, password: String, imageUri: Uri): Result<User>
    suspend fun login(email: String, password: String): Result<User>
    suspend fun updateUser(user: User): Result<Unit>
    suspend fun deleteUser(userId: String): Result<Unit>
    suspend fun getUser(userId: String): User?
}