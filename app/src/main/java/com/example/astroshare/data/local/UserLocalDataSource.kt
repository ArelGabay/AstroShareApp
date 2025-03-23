package com.example.astroshare.data.local

import com.example.astroshare.data.local.db.UserDao
import com.example.astroshare.data.mappers.toEntity
import com.example.astroshare.data.mappers.toModel
import com.example.astroshare.data.model.User

class UserLocalDataSource(private val userDao: UserDao) {

    suspend fun insertUser(user: User) {
        userDao.insertUser(user.toEntity())
    }

    suspend fun getUserById(userId: String): User? {
        return userDao.getUserById(userId)?.toModel()
    }

    suspend fun getAllUsers(): List<User> {
        return userDao.getAllUsers().map { it.toModel() }
    }

    suspend fun updateUser(user: User) {
        userDao.updateUser(user.toEntity())
    }

    suspend fun deleteUser(user: User) {
        userDao.deleteUser(user.toEntity())
    }
}