package com.example.astroshare.data.model

data class User(
    val id: String = "",
    val displayName: String = "",
    val email: String = "",
    val bio: String? = null,
    val lastLogin: Long = 0L,
    val loggedIn: Boolean = false,
    val postsCount: Int = 0,
    val profilePicture: String? = null
)