package com.example.astroshare.viewmodel.trips

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.astroshare.data.repository.TripRepository

class TripsViewModelFactory(
    private val tripRepository: TripRepository
) : ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TripsViewModel::class.java)) {
            return TripsViewModel(tripRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}