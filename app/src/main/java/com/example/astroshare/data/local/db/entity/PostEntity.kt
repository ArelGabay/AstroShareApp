package com.example.astroshare.data.local.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "posts")
data class PostEntity(
    @PrimaryKey val id: String,
    val ownerId: String,
    val title: String,
    val content: String,
    val imageUrl: String? = null,
    val timestamp: Long = 0L
)