package com.example.astroshare.viewmodel.trips

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.astroshare.data.model.Trip
import com.example.astroshare.data.repository.TripRepository
import kotlinx.coroutines.launch

class MyTripsViewModel(private val tripRepository: TripRepository) : ViewModel() {

    private val _myTrips = MutableLiveData<List<Trip>>()
    val myTrips: LiveData<List<Trip>> get() = _myTrips

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> get() = _error

    // Save the current user id once loaded.
    private var currentUserId: String? = null

    // Load trips belonging to a given user.
    fun loadMyTrips(userId: String) {
        currentUserId = userId
        viewModelScope.launch {
            try {
                val allTrips = tripRepository.getTrips()
                _myTrips.value = allTrips.filter { it.ownerId == userId }
                _error.value = null
            } catch (e: Exception) {
                _error.value = e.message
            }
        }
    }

    // In your TripsViewModel (or a similar ViewModel)
    suspend fun getTripById(tripId: String): Trip? {
        return tripRepository.getTripById(tripId)
    }

    // Delete a trip, then reload trips for the current user.
    fun deleteTrip(tripId: String) {
        viewModelScope.launch {
            try {
                val result = tripRepository.deleteTrip(tripId)
                if (result.isSuccess) {
                    currentUserId?.let { loadMyTrips(it) }
                } else {
                    _error.value = result.exceptionOrNull()?.message ?: "Error deleting trip"
                }
            } catch (e: Exception) {
                _error.value = e.message
            }
        }
    }

    // Update a trip, then reload trips for the current user.
    fun updateTrip(tripId: String, updatedTrip: Trip) {
        viewModelScope.launch {
            try {
                val result = tripRepository.updateTrip(tripId, updatedTrip)
                if (result.isSuccess) {
                    currentUserId?.let { loadMyTrips(it) }
                } else {
                    _error.value = result.exceptionOrNull()?.message ?: "Error updating trip"
                }
            } catch (e: Exception) {
                _error.value = e.message
            }
        }
    }
}