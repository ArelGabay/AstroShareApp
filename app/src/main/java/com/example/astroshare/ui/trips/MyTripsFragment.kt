package com.example.astroshare.ui.trips

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.astroshare.AstroShareApp
import com.example.astroshare.databinding.FragmentMyTripsBinding
import com.example.astroshare.data.model.Trip
import com.example.astroshare.viewmodel.trips.MyTripsViewModel
import com.example.astroshare.viewmodel.trips.MyTripsViewModelFactory
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch
import androidx.navigation.fragment.findNavController

class MyTripsFragment : Fragment(), TripsAdapter.TripItemListener {

    private var _binding: FragmentMyTripsBinding? = null
    private val binding get() = _binding!!

    private val myTripsViewModel: MyTripsViewModel by viewModels {
        MyTripsViewModelFactory((requireActivity().application as AstroShareApp).tripRepository)
    }

    private lateinit var tripsAdapter: TripsAdapter
    private val currentUserId = FirebaseAuth.getInstance().currentUser?.uid

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentMyTripsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        tripsAdapter = currentUserId?.let { TripsAdapter(it, listener = this) }!!
        binding.recyclerViewTrips.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = tripsAdapter
        }

        // Get the current user id from FirebaseAuth and load the user's trips
        lifecycleScope.launch {
            myTripsViewModel.loadMyTrips(currentUserId)
        }

        // Observe LiveData updates and update the adapter's list
        myTripsViewModel.myTrips.observe(viewLifecycleOwner) { trips ->
            tripsAdapter.submitList(trips)
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onEditTrip(trip: Trip) {
        val action = MyTripsFragmentDirections.actionMyTripsFragmentToEditTripFragment(trip.id)
        findNavController().navigate(action)
    }

    override fun onDeleteTrip(trip: Trip) {
        Toast.makeText(requireContext(), "Delete: ${trip.title}", Toast.LENGTH_SHORT).show()
        myTripsViewModel.deleteTrip(trip.id)
    }
}