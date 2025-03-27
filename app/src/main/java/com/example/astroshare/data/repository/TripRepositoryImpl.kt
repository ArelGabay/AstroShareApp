package com.example.astroshare.data.repository

import android.net.Uri
import com.example.astroshare.data.local.TripLocalDataSource
import com.example.astroshare.data.mappers.TripMappers.toEntity
import com.example.astroshare.data.mappers.TripMappers.toModel
import com.example.astroshare.data.model.Trip
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class TripRepositoryImpl(
    private val localDataSource: TripLocalDataSource,
    private val firestore: FirebaseFirestore,
    private val cloudinaryService: com.example.astroshare.data.remote.firebase.CloudinaryStorageService,
    private val appContext: android.content.Context
) : TripRepository {

    private val tripsCollection = firestore.collection("trips")

    override suspend fun createTrip(trip: Trip): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            // Add the trip to Firestore
            val docRef = tripsCollection.document()
            val tripWithId = trip.copy(id = docRef.id)
            docRef.set(tripWithId).await()
            // Cache the trip locally
            localDataSource.insertTrip(trip.toEntity())
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getTrips(): List<Trip> = withContext(Dispatchers.IO) {
        try {
            // 1. Fetch trips from Firestore
            val snapshot = tripsCollection
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .get().await()

            // 2. Convert Firestore documents to Trip objects
            val trips = snapshot.toObjects(Trip::class.java)

            try {
                localDataSource.insertTrips(trips.map { it.toEntity() })
            } catch (e: Exception) {
                // Log or ignore local caching failure
            }

            trips // ← return Firestore trips even if cache fails
        } catch (e: Exception) {
            // If there’s an error, return local cached data
            localDataSource.getAllTrips().map { it.toModel() }
        }
    }

    override suspend fun updateTrip(tripId: String, updatedTrip: Trip): Result<Unit> =
        withContext(Dispatchers.IO) {
            try {
                // Update Firestore
                tripsCollection.document(tripId).set(updatedTrip).await()
                // Update the local cache
                localDataSource.updateTrip(updatedTrip.toEntity())
                Result.success(Unit)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

    override suspend fun deleteTrip(tripId: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            // Delete from Firestore
            tripsCollection.document(tripId).delete().await()
            // Delete from local cache if available
            localDataSource.getTripById(tripId)?.let {
                localDataSource.deleteTrip(it)
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun uploadImage(imageUri: Uri): String = withContext(Dispatchers.IO) {
        // Use Cloudinary to upload the trip image; here we pass a tag "trip" to differentiate trip images.
        cloudinaryService.uploadUserImage("trip", imageUri, appContext)
    }

    // New implementation: Retrieve a single trip from Firestore by its ID.
    override suspend fun getTripById(tripId: String): Trip? = withContext(Dispatchers.IO) {
        try {
            val documentSnapshot = tripsCollection.document(tripId).get().await()
            // Return the Trip object (or null if not found)
            documentSnapshot.toObject(Trip::class.java)
        } catch (e: Exception) {
            null
        }
    }
}