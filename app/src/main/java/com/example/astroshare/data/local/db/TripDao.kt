// TripDao.kt
package com.example.astroshare.data.local.db

import androidx.room.*
import com.example.astroshare.data.local.db.entity.TripEntity

@Dao
interface TripDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTrip(trip: TripEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTrips(trips: List<TripEntity>): List<Long>

    @Query("SELECT * FROM trips WHERE id = :tripId")
    suspend fun getTripById(tripId: String): TripEntity?

    @Query("SELECT * FROM trips")
    suspend fun getAllTrips(): List<TripEntity>

    @Update
    suspend fun updateTrip(trip: TripEntity): Int

    @Delete
    suspend fun deleteTrip(trip: TripEntity): Int
}