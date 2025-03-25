// data/local/TripLocalDataSource.kt
package com.example.astroshare.data.local

import com.example.astroshare.data.local.db.TripDao
import com.example.astroshare.data.local.db.entity.TripEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class TripLocalDataSource(private val tripDao: TripDao) {

    suspend fun insertTrip(trip: TripEntity) = withContext(Dispatchers.IO) {
        tripDao.insertTrip(trip)
    }

    suspend fun insertTrips(trips: List<TripEntity>) = withContext(Dispatchers.IO) {
        tripDao.insertTrips(trips)
    }

    suspend fun getTripById(tripId: String) = withContext(Dispatchers.IO) {
        tripDao.getTripById(tripId)
    }

    suspend fun getAllTrips() = withContext(Dispatchers.IO) {
        tripDao.getAllTrips()
    }

    suspend fun updateTrip(trip: TripEntity) = withContext(Dispatchers.IO) {
        tripDao.updateTrip(trip)
    }

    suspend fun deleteTrip(trip: TripEntity) = withContext(Dispatchers.IO) {
        tripDao.deleteTrip(trip)
    }
}