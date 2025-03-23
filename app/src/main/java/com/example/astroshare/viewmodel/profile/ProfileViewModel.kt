// viewmodel/profile/ProfileViewModel.kt
package com.example.astroshare.viewmodel.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.astroshare.data.model.User
import com.example.astroshare.data.repository.UserRepository
import kotlinx.coroutines.launch

class ProfileViewModel(private val userRepository: UserRepository) : ViewModel() {

    private val _user = MutableLiveData<User?>()
    val user: LiveData<User?> get() = _user

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> get() = _loading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> get() = _error

    /**
     * Loads the user's profile from the repository.
     */
    fun loadUser(userId: String) {
        viewModelScope.launch {
            _loading.value = true
            try {
                val fetchedUser = userRepository.getUser(userId)
                _user.value = fetchedUser
                _error.value = null
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _loading.value = false
            }
        }
    }

    /**
     * Updates the user's profile data.
     */
    fun updateProfile(user: User) {
        viewModelScope.launch {
            _loading.value = true
            try {
                val result = userRepository.updateUser(user)
                if (result.isSuccess) {
                    _user.value = user
                    _error.value = null
                } else {
                    _error.value = result.exceptionOrNull()?.message ?: "Error updating profile"
                }
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _loading.value = false
            }
        }
    }
}