package com.example.astroshare.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.astroshare.data.repository.AuthRepository

class HomeViewModel : ViewModel() {
    private val authRepository = AuthRepository()

    private val _userName = MutableLiveData<String>()
    val userName: LiveData<String> get() = _userName

    init {
        loadUser()
    }

    private fun loadUser() {
        val userId = authRepository.getCurrentUserId()
        if (userId != null) {
            authRepository.getUserData { user, _ ->
                if (user != null) {
                    _userName.value = user.displayName
                } else {
                    _userName.value = "Guest"
                }
            }
        } else {
            _userName.value = "Guest"
        }
    }

    fun logout() {
        authRepository.logout()
    }
}