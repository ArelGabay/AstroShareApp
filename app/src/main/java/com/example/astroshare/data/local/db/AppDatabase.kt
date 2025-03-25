package com.example.astroshare.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.astroshare.data.local.db.entity.UserEntity
import com.example.astroshare.data.local.db.entity.TripEntity

@Database(
    entities = [UserEntity::class, TripEntity::class],
    version = 2,
    exportSchema = false)

abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun tripDao(): TripDao
}