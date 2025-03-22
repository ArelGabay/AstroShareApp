// PostDao.kt
package com.example.astroshare.data.local.db

import androidx.room.*
import com.example.astroshare.data.local.db.entity.PostEntity

@Dao
interface PostDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPost(post: PostEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPosts(posts: List<PostEntity>): List<Long>

    @Query("SELECT * FROM posts WHERE id = :postId")
    suspend fun getPostById(postId: String): PostEntity?

    @Query("SELECT * FROM posts")
    suspend fun getAllPosts(): List<PostEntity>

    @Update
    suspend fun updatePost(post: PostEntity): Int

    @Delete
    suspend fun deletePost(post: PostEntity): Int
}