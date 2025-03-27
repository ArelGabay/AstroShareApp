// UserEntity.kt
package com.example.astroshare.data.local.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user")
data class UserEntity(
    @PrimaryKey val id: String,
    val displayName: String,
    val email: String,
    val bio: String?,
    val lastLogin: Long,
    val loggedIn: Boolean,
    val postsCount: Int,
    val profilePicture: String?
)