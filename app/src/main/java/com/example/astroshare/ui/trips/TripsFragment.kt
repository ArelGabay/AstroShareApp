package com.example.astroshare.ui.trips

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.media3.common.util.UnstableApi
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.astroshare.AstroShareApp
import com.example.astroshare.R
import com.example.astroshare.databinding.FragmentTripsBinding
import com.example.astroshare.viewmodel.trips.TripsViewModel
import com.example.astroshare.viewmodel.trips.TripsViewModelFactory
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

class TripsFragment : Fragment(), TripsAdapter.TripItemListener {

    private var _binding: FragmentTripsBinding? = null
    private val binding get() = _binding!!

    private val tripsViewModel: TripsViewModel by viewModels {
        TripsViewModelFactory((requireActivity().application as AstroShareApp).tripRepository)
    }

    // We pass the currentUserId to the adapter so it knows which trips belong to the current user.
    private lateinit var tripsAdapter: TripsAdapter

    @androidx.annotation.OptIn(UnstableApi::class)
    @OptIn(UnstableApi::class)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTripsBinding.inflate(inflater, container, false)
        return binding.root
    }

    @androidx.annotation.OptIn(UnstableApi::class)
    @OptIn(UnstableApi::class)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: run {
            // If no user is logged in, navigate to login.
            findNavController().navigate(TripsFragmentDirections.actionTripsFragmentToLoginFragment())
            return
        }
        // Initialize adapter with the currentUserId and this fragment as the listener.
        tripsAdapter = TripsAdapter(currentUserId, listener = this)
        binding.recyclerViewTrips.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = tripsAdapter
        }

        tripsViewModel.trips.observe(viewLifecycleOwner) { trips ->
            tripsAdapter.submitList(trips)
        }

        tripsViewModel.error.observe(viewLifecycleOwner) { error ->
            error?.let { Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show() }
        }

        tripsViewModel.loadTrips()

        binding.fabUploadTrip.setOnClickListener {
            findNavController().navigate(TripsFragmentDirections.actionTripsFragmentToUploadTripFragment())
        }
    }

    // TripItemListener implementation:
    override fun onEditTrip(trip: com.example.astroshare.data.model.Trip) {
        // Use Safe Args to navigate to the EditTripFragment, passing the trip ID as an argument.
        val action: NavDirections = TripsFragmentDirections.actionTripsFragmentToEditTripFragment(trip.id)
        findNavController().navigate(action)
    }

    override fun onDeleteTrip(trip: com.example.astroshare.data.model.Trip) {
        Toast.makeText(requireContext(), "Delete: ${trip.title}", Toast.LENGTH_SHORT).show()
        tripsViewModel.deleteTrip(trip.id)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}