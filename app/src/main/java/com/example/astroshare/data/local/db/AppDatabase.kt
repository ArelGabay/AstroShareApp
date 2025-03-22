package com.example.astroshare.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.astroshare.data.local.db.entity.UserEntity
import com.example.astroshare.data.local.db.entity.PostEntity

@Database(entities = [UserEntity::class, PostEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun postDao(): PostDao
}