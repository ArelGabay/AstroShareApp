// viewmodel/auth/RegisterViewModel.kt
package com.example.astroshare.viewmodel.auth

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.astroshare.data.model.User
import com.example.astroshare.data.repository.UserRepository
import kotlinx.coroutines.launch

class RegisterViewModel(private val userRepository: UserRepository) : ViewModel() {

    private val _registeredUser = MutableLiveData<User?>()
    val registeredUser: LiveData<User?> get() = _registeredUser

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> get() = _error

    fun registerWithImage(email: String, password: String, displayName: String, imageUri: Uri) {
        viewModelScope.launch {
            val result = userRepository.registerWithImage(email, password, displayName, imageUri)
            if (result.isSuccess) {
                _registeredUser.value = result.getOrNull()
                _error.value = null
            } else {
                _error.value = result.exceptionOrNull()?.message ?: "Registration failed"
            }
        }
    }
}
