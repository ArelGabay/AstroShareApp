// ui/profile/ProfileFragment.kt
package com.example.astroshare.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.astroshare.AstroShareApp
import com.example.astroshare.databinding.FragmentProfileBinding
import com.example.astroshare.viewmodel.profile.ProfileViewModel
import com.example.astroshare.viewmodel.profile.ProfileViewModelFactory

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    // Instantiate ProfileViewModel using a custom factory, retrieving the userRepository from AstroShareApp.
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

        // For example, you can use a hard-coded userId or retrieve it from your auth system.
        val userId = "your_user_id_here" // Replace this with the actual user ID

        // Load the user's profile.
        profileViewModel.loadUser(userId)

        // Observe the user LiveData.
        profileViewModel.user.observe(viewLifecycleOwner) { user ->
            user?.let {
                // Update UI with user data.
                binding.tvUsername.text = it.displayName
                binding.tvEmail.text = it.email
                // Update other UI elements as needed.
            }
        }

        // Observe error LiveData to display any error messages.
        profileViewModel.error.observe(viewLifecycleOwner) { error ->
            error?.let {
                // For example, display the error in a Toast.
                // Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}