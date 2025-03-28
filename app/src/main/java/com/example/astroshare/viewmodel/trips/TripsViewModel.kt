package com.example.astroshare.viewmodel.trips

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.astroshare.data.model.Trip
import com.example.astroshare.data.repository.TripRepository
import kotlinx.coroutines.launch

class TripsViewModel(private val tripRepository: TripRepository) : ViewModel() {

    private val _trips = MutableLiveData<List<Trip>>()
    val trips: LiveData<List<Trip>> = _trips

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> = _loading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    // Loads trips with pagination (page argument is used for paginated data)
    fun loadTrips(page: Int) {
        viewModelScope.launch {
            _loading.value = true
            try {
                val tripsList = tripRepository.getTrips(page)
                _trips.value = tripsList
                _error.value = null
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _loading.value = false
            }
        }
    }

    // Creates a new trip
    fun createTrip(trip: Trip) {
        viewModelScope.launch {
            try {
                val result = tripRepository.createTrip(trip)
                if (result.isSuccess) {
                    loadTrips(1) // Reload trips after creation
                } else {
                    _error.value = result.exceptionOrNull()?.message ?: "Error creating trip"
                }
            } catch (e: Exception) {
                _error.value = e.message
            }
        }
    }

    // Updates an existing trip
    fun updateTrip(tripId: String, updatedTrip: Trip) {
        viewModelScope.launch {
            try {
                val result = tripRepository.updateTrip(tripId, updatedTrip)
                if (result.isSuccess) {
                    loadTrips(1) // Reload trips after update
                } else {
                    _error.value = result.exceptionOrNull()?.message ?: "Error updating trip"
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
                    loadTrips(1) // Reload trips after deletion
                } else {
                    _error.value = result.exceptionOrNull()?.message ?: "Error deleting trip"
                }
            } catch (e: Exception) {
                _error.value = e.message
            }
        }
    }

    // Helper function to get a trip by ID
    suspend fun getTripById(tripId: String): Trip? {
        return tripRepository.getTripById(tripId)
    }

    suspend fun uploadImage(imageUri: Uri): String {
        return tripRepository.uploadImage(imageUri)
    }
}
