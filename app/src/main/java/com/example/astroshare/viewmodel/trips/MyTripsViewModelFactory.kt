package com.example.astroshare.viewmodel.trips

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.astroshare.data.repository.TripRepository

class MyTripsViewModelFactory(private val tripRepository: TripRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MyTripsViewModel::class.java)) {
            return MyTripsViewModel(tripRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}