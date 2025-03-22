// data/local/PostLocalDataSource.kt
package com.example.astroshare.data.local

import com.example.astroshare.data.local.db.PostDao
import com.example.astroshare.data.local.db.entity.PostEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class PostLocalDataSource(private val postDao: PostDao) {

    suspend fun insertPost(post: PostEntity) = withContext(Dispatchers.IO) {
        postDao.insertPost(post)
    }

    suspend fun insertPosts(posts: List<PostEntity>) = withContext(Dispatchers.IO) {
        postDao.insertPosts(posts)
    }

    suspend fun getPostById(postId: String) = withContext(Dispatchers.IO) {
        postDao.getPostById(postId)
    }

    suspend fun getAllPosts() = withContext(Dispatchers.IO) {
        postDao.getAllPosts()
    }

    suspend fun updatePost(post: PostEntity) = withContext(Dispatchers.IO) {
        postDao.updatePost(post)
    }

    suspend fun deletePost(post: PostEntity) = withContext(Dispatchers.IO) {
        postDao.deletePost(post)
    }
}