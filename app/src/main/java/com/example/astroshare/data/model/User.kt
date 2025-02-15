package com.example.astroshare.data.model

data class User(
    val id: String = "",
    val email: String = "",
    val displayName: String = "",
    val profilePicture: String? = null,
    val bio: String? = null,
    val postsCount: Int = 0,
    val lastLogin: Long = System.currentTimeMillis(),
    val isLoggedIn: Boolean = false
)
