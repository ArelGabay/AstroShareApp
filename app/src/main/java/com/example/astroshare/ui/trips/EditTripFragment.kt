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
import androidx.navigation.fragment.navArgs
import com.example.astroshare.AstroShareApp
import com.example.astroshare.R
import com.example.astroshare.data.model.Trip
import com.example.astroshare.databinding.FragmentEditTripBinding
import com.example.astroshare.viewmodel.trips.TripsViewModel
import com.example.astroshare.viewmodel.trips.TripsViewModelFactory
import com.squareup.picasso.Picasso
import kotlinx.coroutines.launch

class EditTripFragment : Fragment() {

    private var _binding: FragmentEditTripBinding? = null
    private val binding get() = _binding!!

    private val args: EditTripFragmentArgs by navArgs()

    private val tripsViewModel: TripsViewModel by viewModels {
        TripsViewModelFactory((requireActivity().application as AstroShareApp).tripRepository)
    }

    private var selectedImageUri: Uri? = null
    private var originalTrip: Trip? = null

    private val pickImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if (uri != null) {
            selectedImageUri = uri
            Picasso.get()
                .load(uri)
                .placeholder(R.drawable.image_loading)
                .error(R.drawable.image_loading)
                .into(binding.ivPreview)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEditTripBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Load existing trip data
        lifecycleScope.launch {
            val trip = tripsViewModel.getTripById(args.tripId)
            if (trip != null) {
                originalTrip = trip
                binding.etTitle.setText(trip.title)
                binding.etLocationName.setText(trip.locationName)
                binding.etLocationDetails.setText(trip.locationDetails)
                binding.etContent.setText(trip.content)
                if (!trip.imageUrl.isNullOrEmpty()) {
                    Picasso.get()
                        .load(trip.imageUrl)
                        .placeholder(R.drawable.image_loading)
                        .error(R.drawable.image_loading)
                        .into(binding.ivPreview)
                }
            } else {
                Toast.makeText(requireContext(), "Trip not found", Toast.LENGTH_SHORT).show()
            }
        }

        // Choose new image
        binding.btnChooseImage.setOnClickListener {
            pickImageLauncher.launch("image/*")
        }

        // Save updated trip
        binding.btnSaveTrip.setOnClickListener {
            val existingTrip = originalTrip
            if (existingTrip == null) {
                Toast.makeText(requireContext(), "Trip not loaded", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val title = binding.etTitle.text.toString().trim()
            val locationName = binding.etLocationName.text.toString().trim()
            val locationDetails = binding.etLocationDetails.text.toString().trim()
            val content = binding.etContent.text.toString().trim()

            if (title.isEmpty() || content.isEmpty() || locationName.isEmpty()) {
                Toast.makeText(requireContext(), "Please fill required fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            lifecycleScope.launch {
                try {
                    val imageUrl = if (selectedImageUri != null) {
                        tripsViewModel.uploadImage(selectedImageUri!!)
                    } else {
                        existingTrip.imageUrl
                    }

                    val updatedTrip = Trip(
                        id = existingTrip.id,
                        ownerId = existingTrip.ownerId, // ✅ preserve original ownerId
                        title = title,
                        content = content,
                        locationName = locationName,
                        locationDetails = locationDetails,
                        imageUrl = imageUrl,
                        timestamp = System.currentTimeMillis()
                    )

                    tripsViewModel.updateTrip(updatedTrip.id, updatedTrip)
                    Toast.makeText(requireContext(), "Trip updated!", Toast.LENGTH_SHORT).show()
                    findNavController().popBackStack()
                } catch (e: Exception) {
                    Toast.makeText(requireContext(), e.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}