// ui/auth/LoginFragment.kt
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

class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    // Instantiate the LoginViewModel using our custom factory.
    private val loginViewModel: LoginViewModel by viewModels {
        LoginViewModelFactory((requireActivity().application as AstroShareApp).userRepository)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnLogin.setOnClickListener {
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()
            if (email.isNotEmpty() && password.isNotEmpty()) {
                loginViewModel.login(email, password)
            } else {
                Toast.makeText(requireContext(), "Please fill in email and password", Toast.LENGTH_SHORT).show()
            }
        }

        // Observe user LiveData to navigate on successful login.
        loginViewModel.user.observe(viewLifecycleOwner) { user ->
            user?.let {
                // Navigate to the profile screen.
                findNavController().navigate(R.id.action_loginFragment_to_profileFragment)            }
        }

        // Observe error LiveData to display error messages.
        loginViewModel.error.observe(viewLifecycleOwner) { error ->
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