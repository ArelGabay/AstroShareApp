// ui/auth/RegisterFragment.kt
package com.example.astroshare.ui.auth

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.astroshare.AstroShareApp
import com.example.astroshare.R
import com.example.astroshare.databinding.FragmentRegisterBinding
import com.example.astroshare.viewmodel.auth.RegisterViewModel
import com.example.astroshare.viewmodel.auth.RegisterViewModelFactory

class RegisterFragment : Fragment() {

    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!

    // Instantiate the RegisterViewModel using our custom factory.
    private val registerViewModel: RegisterViewModel by viewModels {
        RegisterViewModelFactory((requireActivity().application as AstroShareApp).userRepository)
    }

    // Launcher for picking an image from the gallery.
    private var selectedImageUri: Uri? = null
    private val pickImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        if (uri != null) {
            selectedImageUri = uri
            // Set the selected image on the avatar ImageView.
            binding.avatarImageView.setImageURI(uri)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // When the user clicks the avatar, launch the image picker.
        binding.avatarImageView.setOnClickListener {
            pickImageLauncher.launch("image/*")
        }

        // When the Register button is clicked, validate fields and call register.
        binding.registerButton.setOnClickListener {
            val email = binding.editTextEmail.text.toString().trim()
            val password = binding.editTextPassword.text.toString().trim()
            if (email.isNotEmpty() && password.isNotEmpty() && selectedImageUri != null) {
                registerViewModel.registerWithImage(email, password, selectedImageUri!!)
            } else {
                Toast.makeText(requireContext(), "Please fill all fields and select an image", Toast.LENGTH_SHORT).show()
            }
        }

        // Observe the registeredUser LiveData to handle successful registration.
        registerViewModel.registeredUser.observe(viewLifecycleOwner) { user ->
            user?.let {
                Toast.makeText(requireContext(), "Registration successful!", Toast.LENGTH_SHORT).show()
                // Navigate back to Login or to Main screen as needed.
                findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
            }
        }

        // Observe error LiveData to show errors.
        registerViewModel.error.observe(viewLifecycleOwner) { error ->
            error?.let {
                Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}