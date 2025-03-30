package com.example.astroshare.ui.trips

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.astroshare.AstroShareApp
import com.example.astroshare.R
import com.example.astroshare.databinding.FragmentTripsBinding
import com.example.astroshare.viewmodel.trips.TripsViewModel
import com.example.astroshare.viewmodel.trips.TripsViewModelFactory
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch
import com.example.astroshare.data.repository.UserRepository

class TripsFragment : Fragment(), TripsAdapter.TripItemListener {

    private var _binding: FragmentTripsBinding? = null
    private val binding get() = _binding!!

    private val tripsViewModel: TripsViewModel by viewModels {
        TripsViewModelFactory((requireActivity().application as AstroShareApp).tripRepository)
    }

    private val userRepository: UserRepository by lazy {
        (requireActivity().application as AstroShareApp).userRepository
    }

    private lateinit var tripsAdapter: TripsAdapter
    private val currentUserId = FirebaseAuth.getInstance().currentUser?.uid

    private var isLoading = false
    private var isLastPage = false
    private var currentPage = 1

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTripsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (currentUserId == null) {
            // If no user is logged in, navigate to login.
            findNavController().navigate(TripsFragmentDirections.actionTripsFragmentToLoginFragment())
            return
        }

        // Initialize adapter with the currentUserId and this fragment as the listener.
        tripsAdapter = TripsAdapter(currentUserId, listener = this)
        binding.recyclerViewTrips.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = tripsAdapter
            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)

                    val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                    val visibleItemCount = layoutManager.childCount
                    val totalItemCount = layoutManager.itemCount
                    val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()

                    if (!isLoading && !isLastPage) {
                        if (firstVisibleItemPosition + visibleItemCount >= totalItemCount) {
                            loadMoreTrips()
                        }
                    }
                }
            })
        }

        // Observe trips once
        tripsViewModel.trips.observe(viewLifecycleOwner) { trips ->
            tripsAdapter.submitList(trips)
            isLoading = false
            binding.progressBar.visibility = View.GONE  // Hide spinner when done
            isLastPage = trips.isEmpty()
        }

        loadTrips()

        binding.fabUploadTrip.setOnClickListener {
            findNavController().navigate(TripsFragmentDirections.actionTripsFragmentToUploadTripFragment())
        }
    }

    private fun loadTrips() {
        isLoading = true
        binding.progressBar.visibility = View.VISIBLE  // Show spinner when loading starts
        lifecycleScope.launch {
            tripsViewModel.loadTrips(currentPage) // Pass current page number
        }
    }

    private fun loadMoreTrips() {
        if (isLoading || isLastPage) return
        currentPage++
        loadTrips()
    }

    override fun onEditTrip(trip: com.example.astroshare.data.model.Trip) {
        val action: NavDirections = TripsFragmentDirections.actionTripsFragmentToEditTripFragment(trip.id)
        findNavController().navigate(action)
    }

    override fun onDeleteTrip(trip: com.example.astroshare.data.model.Trip) {
        Toast.makeText(requireContext(), "Delete: ${trip.title}", Toast.LENGTH_SHORT).show()

        lifecycleScope.launch {
            val user = userRepository.getUser(trip.ownerId)
            if (user != null) {
                val updatedUser = user.copy(postsCount = user.postsCount - 1)
                userRepository.updateUser(updatedUser)
            }
        }

        tripsViewModel.deleteTrip(trip.id)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
