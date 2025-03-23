// viewmodel/posts/PostsViewModel.kt
package com.example.astroshare.viewmodel.posts

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.astroshare.data.model.Post
import com.example.astroshare.data.repository.PostRepository
import kotlinx.coroutines.launch

class PostsViewModel(private val postRepository: PostRepository) : ViewModel() {

    // LiveData for posts list
    private val _posts = MutableLiveData<List<Post>>()
    val posts: LiveData<List<Post>> = _posts

    // LiveData for loading state
    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> = _loading

    // LiveData for error messages
    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    /**
     * Loads posts from the repository.
     */
    fun loadPosts() {
        viewModelScope.launch {
            _loading.value = true
            try {
                val postsList = postRepository.getPosts()
                _posts.value = postsList
                _error.value = null
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _loading.value = false
            }
        }
    }

    /**
     * Creates a new post.
     */
    fun createPost(post: Post) {
        viewModelScope.launch {
            try {
                val result = postRepository.createPost(post)
                if (result.isSuccess) {
                    // Optionally reload posts to update UI
                    loadPosts()
                } else {
                    _error.value = result.exceptionOrNull()?.message ?: "Error creating post"
                }
            } catch (e: Exception) {
                _error.value = e.message
            }
        }
    }

    /**
     * Updates an existing post.
     */
    fun updatePost(postId: String, updatedPost: Post) {
        viewModelScope.launch {
            try {
                val result = postRepository.updatePost(postId, updatedPost)
                if (result.isSuccess) {
                    loadPosts()
                } else {
                    _error.value = result.exceptionOrNull()?.message ?: "Error updating post"
                }
            } catch (e: Exception) {
                _error.value = e.message
            }
        }
    }

    /**
     * Deletes a post.
     */
    fun deletePost(postId: String) {
        viewModelScope.launch {
            try {
                val result = postRepository.deletePost(postId)
                if (result.isSuccess) {
                    loadPosts()
                } else {
                    _error.value = result.exceptionOrNull()?.message ?: "Error deleting post"
                }
            } catch (e: Exception) {
                _error.value = e.message
            }
        }
    }

    /**
     * Uploads an image and returns its download URL.
     * You can call this function from your UI (if needed) to first upload the image, then create the post.
     */
    suspend fun uploadImage(imageUri: Uri): String {
        return postRepository.uploadImage(imageUri)
    }
}