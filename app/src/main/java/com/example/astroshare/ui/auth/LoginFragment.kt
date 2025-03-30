package com.example.astroshare.ui.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.astroshare.AstroShareApp
import com.example.astroshare.R
import com.example.astroshare.databinding.FragmentLoginBinding
import com.example.astroshare.viewmodel.auth.LoginViewModel
import com.example.astroshare.viewmodel.auth.LoginViewModelFactory
import com.google.firebase.auth.FirebaseAuth

class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    // Instantiate the LoginViewModel using our custom factory.
    private val loginViewModel: LoginViewModel by viewModels {
        LoginViewModelFactory((requireActivity().application as AstroShareApp).userRepository)
    }

    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize FirebaseAuth instance
        firebaseAuth = FirebaseAuth.getInstance()

        // Check if the user is already logged in
        val currentUser = firebaseAuth.currentUser
        if (currentUser != null) {
            // Optional: if user data exists in Room here before navigating
            Toast.makeText(requireContext(), "Welcome back, ${currentUser.email}", Toast.LENGTH_SHORT).show()

            // Navigate straight to mainFlow
            // If login is successful:
            //Good one
            findNavController().navigate(LoginFragmentDirections.actionLoginFragmentToMainFlow())
            return // Don't continue with setting up login UI if already logged in
        }

        // Login button click listener
        binding.btnLogin.setOnClickListener {
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()
            if (email.isNotEmpty() && password.isNotEmpty()) {
                binding.progressBar.visibility = View.VISIBLE  // Show spinner
                loginViewModel.login(email, password)
            } else {
                Toast.makeText(requireContext(), "Please fill in email and password", Toast.LENGTH_SHORT).show()
            }
        }

        // Register link click listener
        binding.btnRegisterLink.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
        }

        // Observe user LiveData to navigate on successful login.
        loginViewModel.user.observe(viewLifecycleOwner) { user ->
            binding.progressBar.visibility = View.VISIBLE  // Show spinner
            user?.let {
                Toast.makeText(requireContext(), "Login successful!", Toast.LENGTH_SHORT).show()
                findNavController().navigate(R.id.action_loginFragment_to_mainFlow)
            }
        }

        // Observe error LiveData to display error messages.
        loginViewModel.error.observe(viewLifecycleOwner) { error ->
            binding.progressBar.visibility = View.VISIBLE  // Show spinner
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