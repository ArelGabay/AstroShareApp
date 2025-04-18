package com.example.astroshare.ui.trips

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.astroshare.AstroShareApp
import com.example.astroshare.R
import com.example.astroshare.data.model.Trip
import com.example.astroshare.databinding.FragmentUploadtripBinding
import com.example.astroshare.viewmodel.trips.TripsViewModel
import com.example.astroshare.viewmodel.trips.TripsViewModelFactory
import com.google.firebase.auth.FirebaseAuth
import com.squareup.picasso.Picasso
import kotlinx.coroutines.launch
import com.example.astroshare.data.repository.UserRepository

class UploadTripFragment : Fragment() {

    private var _binding: FragmentUploadtripBinding? = null
    private val binding get() = _binding!!

    private var selectedImageUri: Uri? = null

    private val tripsViewModel: TripsViewModel by viewModels {
        TripsViewModelFactory((requireActivity().application as AstroShareApp).tripRepository)
    }
    private val userRepository: UserRepository by lazy {
        (requireActivity().application as AstroShareApp).userRepository
    }

    // (The image picker code remains unchanged)
    private val pickImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if (uri != null) {
            selectedImageUri = uri
            Picasso.get()
                .load(uri)
                .placeholder(R.drawable.avatar)
                .error(R.drawable.avatar)
                .fit()
                .centerCrop()
                .into(binding.ivPreview)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentUploadtripBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnChooseImage.setOnClickListener { pickImageLauncher.launch("image/*") }
        binding.btnUploadTrip.setOnClickListener { uploadTrip() }

        tripsViewModel.error.observe(viewLifecycleOwner) { errorMsg ->
            errorMsg?.let { Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show() }
        }

        binding.btnBackToLogin.setOnClickListener {
            findNavController().navigateUp() // goes back in nav stack
        }
    }

    private fun uploadTrip() {
        val title = binding.etTitle.text.toString().trim()
        val locationName = binding.etLocationName.text.toString().trim()
        val locationDetails = binding.etLocationDetails.text.toString().trim()
        val content = binding.etContent.text.toString().trim()

        if (title.isEmpty() || content.isEmpty() || locationName.isEmpty()) {
            Toast.makeText(requireContext(), "Please fill required fields", Toast.LENGTH_SHORT).show()
            return
        }
        if (selectedImageUri == null) {
            Toast.makeText(requireContext(), "Please choose an image!", Toast.LENGTH_SHORT).show()
            return
        }

        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser == null) {
            Toast.makeText(requireContext(), "User not logged in", Toast.LENGTH_SHORT).show()
            return
        }
        val ownerId = currentUser.uid

        // Show spinner on the card
        binding.progressBar.visibility = View.VISIBLE

        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val imageUrl = tripsViewModel.uploadImage(selectedImageUri!!)
                val newTrip = Trip(
                    id = "",
                    ownerId = ownerId,
                    title = title,
                    content = content,
                    imageUrl = imageUrl,
                    timestamp = System.currentTimeMillis(),
                    locationName = locationName,
                    locationDetails = locationDetails
                )
                // Update user postsCount
                val user = userRepository.getUser(ownerId)
                if (user != null) {
                    val updatedUser = user.copy(postsCount = user.postsCount + 1)
                    userRepository.updateUser(updatedUser)
                }
                tripsViewModel.createTrip(newTrip)
                clearFields()

                // Show toast before navigation and use application context if needed
                if (isAdded) {
                    Toast.makeText(requireContext(), "Trip uploaded!", Toast.LENGTH_SHORT).show()
                }

                //Toast.makeText(requireContext(), "Trip uploaded!", Toast.LENGTH_SHORT).show()
                //requireActivity().onBackPressed() // Navigate back
                findNavController().popBackStack()
            } catch (e: Exception) {
                Toast.makeText(requireContext(), e.message, Toast.LENGTH_SHORT).show()
            } finally {
                // Hide spinner in both success and error cases
                binding.progressBar.visibility = View.GONE
            }
        }
    }

    private fun clearFields() {
        binding.etTitle.setText("")
        binding.etLocationName.setText("")
        binding.etLocationDetails.setText("")
        binding.etContent.setText("")
        binding.ivPreview.setImageResource(R.drawable.avatar)
        selectedImageUri = null
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}