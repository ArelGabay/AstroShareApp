// data/repository/PostRepositoryImpl.kt
package com.example.astroshare.data.repository

import android.net.Uri
import com.example.astroshare.data.local.PostLocalDataSource
import com.example.astroshare.data.mappers.PostMappers.toEntity
import com.example.astroshare.data.mappers.PostMappers.toModel
import com.example.astroshare.data.model.Post
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class PostRepositoryImpl(
    private val localDataSource: PostLocalDataSource,
    private val firestore: FirebaseFirestore,
    private val storage: FirebaseStorage
) : PostRepository {

    private val postsCollection = firestore.collection("posts")
    private val storageReference = storage.reference.child("post_images")

    override suspend fun createPost(post: Post): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            // Add post remotely (Firestore)
            postsCollection.add(post).await()

            // Convert domain Post -> PostEntity, then insert locally
            localDataSource.insertPost(post.toEntity())
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getPosts(): List<Post> = withContext(Dispatchers.IO) {
        try {
            // Fetch from Firestore
            val snapshot = postsCollection
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .get().await()
            val posts = snapshot.toObjects(Post::class.java)

            // Convert to PostEntity and store locally
            localDataSource.insertPosts(posts.map { it.toEntity() })

            // Return domain models from Firestore
            posts
        } catch (e: Exception) {
            // On error, fallback to local data
            localDataSource.getAllPosts().map { it.toModel() }
        }
    }

    override suspend fun updatePost(postId: String, updatedPost: Post): Result<Unit> =
        withContext(Dispatchers.IO) {
            try {
                // Update Firestore
                postsCollection.document(postId).set(updatedPost).await()

                // Update local DB
                localDataSource.updatePost(updatedPost.toEntity())
                Result.success(Unit)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

    override suspend fun deletePost(postId: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            // Delete from Firestore
            postsCollection.document(postId).delete().await()

            // Also delete locally, if you wish:
            val localPost = localDataSource.getPostById(postId)
            if (localPost != null) {
                localDataSource.deletePost(localPost)
            }

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun uploadImage(imageUri: Uri): String = withContext(Dispatchers.IO) {
        val fileReference = storageReference.child("${System.currentTimeMillis()}.jpg")
        fileReference.putFile(imageUri).await()
        fileReference.downloadUrl.await().toString()
    }
}