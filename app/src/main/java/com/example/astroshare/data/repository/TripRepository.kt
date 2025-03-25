package com.example.astroshare.data.repository

import android.net.Uri
import com.example.astroshare.data.model.Trip

interface TripRepository {
    suspend fun createTrip(trip: Trip): Result<Unit>
    suspend fun getTrips(): List<Trip>
    suspend fun updateTrip(tripId: String, updatedTrip: Trip): Result<Unit>
    suspend fun deleteTrip(tripId: String): Result<Unit>
    suspend fun uploadImage(imageUri: Uri): String
}
