package com.example.astroshare.ui.profile

import android.net.Uri
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.example.astroshare.databinding.FragmentEditProfileBinding
import com.example.astroshare.R
import com.example.astroshare.AstroShareApp
import com.squareup.picasso.Picasso
import com.google.firebase.auth.FirebaseAuth
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import kotlinx.coroutines.launch

class EditProfileFragment : Fragment() {

    private var _binding: FragmentEditProfileBinding? = null
    private val binding get() = _binding!!

    private var selectedImageUri: Uri? = null
    private val userRepository by lazy {
        (requireActivity().application as AstroShareApp).userRepository
    }

    private val appContext by lazy {
        requireActivity().applicationContext
    }

    private val pickImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if (uri != null) {
            selectedImageUri = uri
            Picasso.get()
                .load(uri)
                .placeholder(R.drawable.avatar)
                .error(R.drawable.avatar)
                .into(binding.ivProfilePic)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentEditProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: return

        lifecycleScope.launch {
            val user = userRepository.getUser(currentUserId)
            user?.let {
                binding.etUsername.setText(it.displayName)
                if (!it.profilePicture.isNullOrEmpty()) {
                    Picasso.get().load(it.profilePicture).into(binding.ivProfilePic)
                }
            }
        }

        binding.btnChooseProfileImage.setOnClickListener {
            pickImageLauncher.launch("image/*")
        }

        binding.btnSaveProfile.setOnClickListener {
            val newName = binding.etUsername.text.toString().trim()

            if (newName.isEmpty()) {
                Toast.makeText(requireContext(), "Name can't be empty", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Show the spinner overlay
            binding.progressBar.visibility = View.VISIBLE

            lifecycleScope.launch {
                try {
                    val user = userRepository.getUser(currentUserId)
                    if (user != null) {
                        // Upload new image if selected
                        val newProfileUrl = if (selectedImageUri != null) {
                            // Upload via Cloudinary through UserRepository
                            (userRepository as com.example.astroshare.data.repository.UserRepositoryImpl)
                                .cloudinaryService
                                .uploadUserImage(user.id, selectedImageUri!!, appContext)
                        } else {
                            user.profilePicture
                        }

                        val updatedUser = user.copy(
                            displayName = newName,
                            profilePicture = newProfileUrl
                        )

                        userRepository.updateUser(updatedUser)

                        Toast.makeText(requireContext(), "Profile updated!", Toast.LENGTH_SHORT).show()
                        findNavController().popBackStack()
                    }
                } catch (e: Exception) {
                    Toast.makeText(requireContext(), e.message ?: "Error updating profile", Toast.LENGTH_SHORT).show()
                } finally {
                    // Hide the spinner regardless of outcome
                    binding.progressBar.visibility = View.GONE
                }
            }
        }

        binding.btnBackToLogin.setOnClickListener {
            findNavController().navigateUp() // goes back in nav stack
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}