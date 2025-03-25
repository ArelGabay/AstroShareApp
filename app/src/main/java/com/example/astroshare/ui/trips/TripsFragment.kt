package com.example.astroshare.ui.trips

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.OptIn
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.media3.common.util.UnstableApi
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.astroshare.AstroShareApp
import com.example.astroshare.R
import com.example.astroshare.data.model.Trip
import com.example.astroshare.databinding.FragmentTripsBinding
import com.example.astroshare.viewmodel.trips.TripsViewModel
import com.example.astroshare.viewmodel.trips.TripsViewModelFactory
import kotlinx.coroutines.launch

class TripsFragment : Fragment() {

    private var _binding: FragmentTripsBinding? = null
    private val binding get() = _binding!!

    private val tripsViewModel: TripsViewModel by viewModels {
        TripsViewModelFactory((requireActivity().application as AstroShareApp).tripRepository)
    }

    private lateinit var tripsAdapter: TripsAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the updated fragment_trips.xml with the FAB
        _binding = FragmentTripsBinding.inflate(inflater, container, false)
        return binding.root
    }

    @OptIn(UnstableApi::class)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize adapter & set up RecyclerView
        tripsAdapter = TripsAdapter()
        binding.recyclerViewTrips.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = tripsAdapter
        }

        // Observe trips
        tripsViewModel.trips.observe(viewLifecycleOwner) { trips ->
            tripsAdapter.submitList(trips)
        }

        // Observe errors
        tripsViewModel.error.observe(viewLifecycleOwner) { error ->
            error?.let { Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show() }
        }

        // Load trips
        tripsViewModel.loadTrips()

        // FAB click -> navigate to UploadTripFragment
        binding.fabUploadTrip.setOnClickListener {
            findNavController().navigate(R.id.uploadTripFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}