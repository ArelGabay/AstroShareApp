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
import com.google.firebase.auth.FirebaseAuth
import com.squareup.picasso.Picasso

class RegisterFragment : Fragment() {

    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!

    private val registerViewModel: RegisterViewModel by viewModels {
        RegisterViewModelFactory((requireActivity().application as AstroShareApp).userRepository)
    }

    private var selectedImageUri: Uri? = null

    private val pickImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        if (uri != null) {
            selectedImageUri = uri
            Picasso.get()
                .load(uri)
                .placeholder(R.drawable.avatar)
                .error(R.drawable.avatar)
                .resize(120, 120)
                .centerCrop()
                .into(binding.avatarImageView)
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

        // Set default avatar
        Picasso.get()
            .load(R.drawable.avatar)
            .resize(120, 120)
            .centerCrop()
            .into(binding.avatarImageView)

        binding.avatarImageView.setOnClickListener {
            pickImageLauncher.launch("image/*")
        }

        binding.registerButton.setOnClickListener {
            val name = binding.editTextName.text.toString().trim()
            val email = binding.editTextEmail.text.toString().trim()
            val password = binding.editTextPassword.text.toString().trim()

            if (name.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty() && selectedImageUri != null) {
                registerViewModel.registerWithImage(email, password, name, selectedImageUri!!)
            } else {
                Toast.makeText(requireContext(), "Please fill all fields and select an image", Toast.LENGTH_SHORT).show()
            }
        }

        // Observe registration result
        registerViewModel.registeredUser.observe(viewLifecycleOwner) { user ->
            user?.let {
                // Sign out so that LoginFragment does not auto-redirect to profile
                FirebaseAuth.getInstance().signOut()
                Toast.makeText(requireContext(), "Registration successful! Please login.", Toast.LENGTH_SHORT).show()
                findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
            }
        }

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