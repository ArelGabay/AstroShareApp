package com.example.astroshare.data.mappers

import com.example.astroshare.data.local.db.entity.UserEntity
import com.example.astroshare.data.model.User

fun UserEntity.toModel(): User {
    return User(
        id = this.id,
        displayName = this.displayName,
        email = this.email,
        bio = this.bio,
        lastLogin = this.lastLogin,
        loggedIn = this.loggedIn,
        postsCount = this.postsCount,
        profilePicture = this.profilePicture
    )
}

fun User.toEntity(): UserEntity {
    return UserEntity(
        id = this.id,
        displayName = this.displayName,
        email = this.email,
        bio = this.bio,
        lastLogin = this.lastLogin,
        loggedIn = this.loggedIn,
        postsCount = this.postsCount,
        profilePicture = this.profilePicture
    )
}