package com.example.astroshare.ui.profile

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
import com.example.astroshare.databinding.FragmentProfileBinding
import com.example.astroshare.viewmodel.profile.ProfileViewModel
import com.example.astroshare.viewmodel.profile.ProfileViewModelFactory
import com.google.firebase.auth.FirebaseAuth

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    // FirebaseAuth instance
    private lateinit var auth: FirebaseAuth

    private val profileViewModel: ProfileViewModel by viewModels {
        ProfileViewModelFactory((requireActivity().application as AstroShareApp).userRepository)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize FirebaseAuth
        auth = FirebaseAuth.getInstance()

        // Get the current user
        val firebaseUser = auth.currentUser
        if (firebaseUser == null) {
            // If no user is logged in, go back to LoginFragment
            findNavController().navigate(R.id.action_profileFragment_to_loginFragment)
            return
        }

        val userId = firebaseUser.uid

        // Load the user's profile from your Room/Firestore via ViewModel
        profileViewModel.loadUser(userId)

        // Observe the user LiveData
        profileViewModel.user.observe(viewLifecycleOwner) { user ->
            user?.let {
                binding.tvUsername.text = it.displayName
                binding.tvEmail.text = it.email
            }
        }

        // Observe any errors
        profileViewModel.error.observe(viewLifecycleOwner) { error ->
            error?.let {
                Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
            }
        }

        // Logout button logic
        binding.btnLogout.setOnClickListener {
            auth.signOut()
            Toast.makeText(requireContext(), "Logged out", Toast.LENGTH_SHORT).show()

            // Navigate back to LoginFragment after logout
            findNavController().navigate(R.id.action_profileFragment_to_loginFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}