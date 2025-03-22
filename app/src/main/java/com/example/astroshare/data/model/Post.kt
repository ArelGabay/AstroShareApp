package com.example.astroshare.data.model

data class Post(
    val id: String = "",
    val ownerId: String = "",
    val title: String = "",
    val content: String = "",
    val imageUrl: String? = null,
    val timestamp: Long = 0L
)

