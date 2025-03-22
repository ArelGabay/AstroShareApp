// data/repository/PostRepository.kt
package com.example.astroshare.data.repository

import android.net.Uri
import com.example.astroshare.data.model.Post

interface PostRepository {
    suspend fun createPost(post: Post): Result<Unit>
    suspend fun getPosts(): List<Post>
    suspend fun updatePost(postId: String, updatedPost: Post): Result<Unit>
    suspend fun deletePost(postId: String): Result<Unit>
    suspend fun uploadImage(imageUri: Uri): String
}
