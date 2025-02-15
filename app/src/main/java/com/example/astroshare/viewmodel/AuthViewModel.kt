package com.example.astroshare.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.astroshare.data.repository.AuthRepository
import com.example.astroshare.data.model.User

class AuthViewModel : ViewModel() {
    private val authRepository = AuthRepository()

    private val _authState = MutableLiveData<Boolean>()  // Tracks login/register status
    val authState: LiveData<Boolean> = _authState

    private val _errorMessage = MutableLiveData<String?>()  // Tracks errors
    val errorMessage: LiveData<String?> = _errorMessage

    private val _loggedInUser = MutableLiveData<User?>()  // Stores logged-in user
    val loggedInUser: LiveData<User?> = _loggedInUser

    // Register User
    fun register(email: String, password: String, name: String) {
        authRepository.registerUser(email, password, name) { success, error ->
            if (success) {
                _authState.value = true
                _errorMessage.value = null
            } else {
                _authState.value = false
                _errorMessage.value = error
            }
        }
    }

    // Login User
    fun login(email: String, password: String) {
        authRepository.loginUser(email, password) { user, error ->
            if (user != null) {
                _loggedInUser.value = user
                _authState.value = true
                _errorMessage.value = null
            } else {
                _authState.value = false
                _errorMessage.value = error
            }
        }
    }

    // Logout
    fun logout() {
        authRepository.logout()
        _loggedInUser.value = null
        _authState.value = false
    }
}