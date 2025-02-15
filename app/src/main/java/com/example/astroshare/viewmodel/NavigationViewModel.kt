package com.example.astroshare.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

enum class NavigationDestination {
    LOGIN, REGISTER, HOME
}

class NavigationViewModel : ViewModel() {

    private val _navigationEvent = MutableLiveData<NavigationDestination>()
    val navigationEvent: LiveData<NavigationDestination> = _navigationEvent

    fun navigateTo(destination: NavigationDestination) {
        _navigationEvent.value = destination
    }
}