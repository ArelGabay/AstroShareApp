package com.example.astroshare.viewmodel.trips

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.astroshare.data.model.Trip
import com.example.astroshare.data.repository.TripRepository
import kotlinx.coroutines.launch

class MyTripsViewModel(private val tripRepository: TripRepository) : ViewModel() {

    private val _myTrips = MutableLiveData<List<Trip>>()
    val myTrips: LiveData<List<Trip>> = _myTrips

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> = _loading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    private var currentPage = 1 // Keep track of current page for pagination
    private val itemsPerPage = 20 // Define the number of items per page

    // Modify loadMyTrips to include pagination and user filtering
    fun loadMyTrips(userId: String) {
        viewModelScope.launch {
            _loading.value = true
            try {
                // Get all trips from the repository
                val tripsList = tripRepository.getTrips(currentPage)

                // Apply filtering by userId to get the user's trips
                val filteredTrips = tripsList.filter { it.ownerId == userId }

                // Apply pagination by slicing the list based on currentPage and itemsPerPage
                val startIndex = (currentPage - 1) * itemsPerPage
                val endIndex = startIndex + itemsPerPage
                val paginatedTrips = filteredTrips.subList(startIndex, minOf(endIndex, filteredTrips.size))

                _myTrips.value = paginatedTrips
                _error.value = null
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _loading.value = false
            }
        }
    }

    // Handle loading next page
    fun loadNextPage(userId: String) {
        currentPage++
        loadMyTrips(userId)
    }

    // Creates a new trip
    fun createTrip(trip: Trip) {
        viewModelScope.launch {
            try {
                val result = tripRepository.createTrip(trip)
                if (result.isSuccess) {
                    loadMyTrips(trip.ownerId) // Reload after creating
                } else {
                    _error.value = result.exceptionOrNull()?.message ?: "Error creating trip"
                }
            } catch (e: Exception) {
                _error.value = e.message
            }
        }
    }

    // Deletes a trip
    fun deleteTrip(tripId: String) {
        viewModelScope.launch {
            try {
                val result = tripRepository.deleteTrip(tripId)
                if (result.isSuccess) {
                    loadMyTrips("someUserId") // Reload after deletion
                } else {
                    _error.value = result.exceptionOrNull()?.message ?: "Error deleting trip"
                }
            } catch (e: Exception) {
                _error.value = e.message
            }
        }
    }

    // Update trip
    fun updateTrip(tripId: String, updatedTrip: Trip) {
        viewModelScope.launch {
            try {
                val result = tripRepository.updateTrip(tripId, updatedTrip)
                if (result.isSuccess) {
                    loadMyTrips(updatedTrip.ownerId) // Reload after update
                } else {
                    _error.value = result.exceptionOrNull()?.message ?: "Error updating trip"
                }
            } catch (e: Exception) {
                _error.value = e.message
            }
        }
    }

    suspend fun uploadImage(imageUri: Uri): String {
        return tripRepository.uploadImage(imageUri)
    }
}
