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
import com.squareup.picasso.Picasso

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
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
        auth = FirebaseAuth.getInstance()

        val firebaseUser = auth.currentUser
        if (firebaseUser == null) {
          findNavController().navigate(R.id.loginFragment)
            return
        }
        val userId = firebaseUser.uid

        // Load user profile from local DB (and Firestore as fallback)
        profileViewModel.loadUser(userId)

        profileViewModel.user.observe(viewLifecycleOwner) { user ->
            user?.let {
                binding.tvUsername.text = it.displayName
                binding.tvEmail.text = it.email

                if (!it.profilePicture.isNullOrEmpty()) {
                    Picasso.get()
                        .load(it.profilePicture)
                        .placeholder(R.drawable.avatar)
                        .error(R.drawable.avatar)
                        .noFade()       // Disable fade-in animation for faster visual response
                        .fit()          // Resize the image to fit the ImageView
                        .centerCrop()   // Crop the image centered
                        .into(binding.ivProfilePicture)
                }
            }
        }

        profileViewModel.error.observe(viewLifecycleOwner) { error ->
            error?.let {
                Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
            }
        }

        binding.btnLogout.setOnClickListener {
            auth.signOut()
            Toast.makeText(requireContext(), "Logged out", Toast.LENGTH_SHORT).show()
            // Navigate back to login
            findNavController().navigate(R.id.loginFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}