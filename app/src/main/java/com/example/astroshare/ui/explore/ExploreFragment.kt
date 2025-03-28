package com.example.astroshare.ui.explore

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ToggleButton
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.astroshare.R
import com.example.astroshare.databinding.FragmentExploreBinding
import com.example.astroshare.data.repository.BodiesEventsRepository
import com.example.astroshare.data.repository.CombinedBodiesEventsResponse
import com.example.astroshare.data.repository.MoonPhaseRepository
import com.example.astroshare.data.repository.StarChartRepository
import kotlinx.coroutines.launch
import java.net.SocketTimeoutException

class ExploreFragment : Fragment() {


    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1001
    }

    // View binding instance
    private var _binding: FragmentExploreBinding? = null
    private val binding get() = _binding!!

    // Hold the combined events response so the toggle can update the list
    private var combinedEvents: CombinedBodiesEventsResponse? = null

    // Hold the image URLs for Moon Phase and Star Chart
    private var moonPhaseImageUrl: String? = null
    private var starChartImageUrl: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?

    ): View {
        _binding = FragmentExploreBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Handle system insets so scrollView doesn't get cut off by the nav bar
        ViewCompat.setOnApplyWindowInsetsListener(binding.scrollView) { view, insets ->
            val bottomInset = insets.getInsets(WindowInsetsCompat.Type.systemBars()).bottom
            view.setPadding(
                view.paddingLeft,
                view.paddingTop,
                view.paddingRight,
                bottomInset
            )
            insets
        }

        // Set up RecyclerView for multi-card display
        binding.eventsRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        // Setup toggle button listener to update events list when toggled.
        (binding.bodyToggleButton as ToggleButton).setOnCheckedChangeListener { _, isChecked ->
            // "Sun" if checked; "Moon" if not
            val selectedBody = if (isChecked) "sun" else "moon"
            updateCards(selectedBody)
        }

        // Check location permission
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
        } else {
            Log.d("ExploreFragment", "Location permission already granted")
            fetchMoonPhaseData()
            fetchStarChartData()
            fetchBodiesEventsData()
        }
    }

    private fun fetchMoonPhaseData() {
        val moonPhaseRepository = MoonPhaseRepository(requireContext())
        val pbMoon = binding.root.findViewById<android.widget.ProgressBar>(R.id.progressBarMoon)
        pbMoon.visibility = View.VISIBLE
        lifecycleScope.launch {
            try {
                Log.d("ExploreFragment", "Calling fetchMoonPhase()")
                val response = moonPhaseRepository.fetchMoonPhase()
                if (response == null) {
                    Log.d("ExploreFragment", "fetchMoonPhase returned null")
                } else {
                    moonPhaseImageUrl = response.data.imageUrl
                    Log.d("ExploreFragment", "Fetched MoonPhase URL: $moonPhaseImageUrl")
                }
                response?.let {
                    Glide.with(requireContext())
                        .load(it.data.imageUrl)
                        .into(binding.moonPhaseImageView)
                }
            } catch (e: SocketTimeoutException) {
                Log.d("ExploreFragment", "fetchMoonPhase timed out: ${e.message}")
            } catch (e: Exception) {
                Log.d("ExploreFragment", "Exception fetching MoonPhase data", e)
            } finally {
                pbMoon.visibility = View.GONE
                // Removed updateCards call here
            }
        }
    }

    private fun fetchStarChartData() {
        val starChartRepository = StarChartRepository(requireContext())
        val pbStarChart = binding.root.findViewById<android.widget.ProgressBar>(R.id.progressBarStarChart)
        pbStarChart.visibility = View.VISIBLE
        lifecycleScope.launch {
            try {
                Log.d("ExploreFragment", "Calling fetchStarChart()")
                val response = starChartRepository.fetchStarChart()
                if (response == null) {
                    Log.d("ExploreFragment", "fetchStarChart returned null")
                } else {
                    starChartImageUrl = response.data.imageUrl
                    Log.d("ExploreFragment", "Fetched StarChart URL: $starChartImageUrl")
                }
                response?.let {
                    Glide.with(requireContext())
                        .load(it.data.imageUrl)
                        .into(binding.starChartImageView)
                }
            } catch (e: SocketTimeoutException) {
                Log.d("ExploreFragment", "fetchStarChart timed out: ${e.message}")
            } catch (e: Exception) {
                Log.d("ExploreFragment", "Exception fetching StarChart data", e)
            } finally {
                pbStarChart.visibility = View.GONE
                // Removed updateCards call here
            }
        }
    }

    // Fetch combined events for Sun and Moon
    private fun fetchBodiesEventsData() {
        val bodiesEventsRepository = BodiesEventsRepository(requireContext())
        val pbEvents = binding.root.findViewById<android.widget.ProgressBar>(R.id.progressBarEvents)
        pbEvents.visibility = View.VISIBLE
        lifecycleScope.launch {
            try {
                Log.d("ExploreFragment", "Calling fetchBodiesEventsData()")
                val response = bodiesEventsRepository.fetchBodiesEventsForSunAndMoon()
                if (response == null) {
                    Log.d("ExploreFragment", "fetchBodiesEventsData returned null")
                    binding.eventsRecyclerView.adapter = null
                } else {
                    combinedEvents = response
                    updateCards(getSelectedBody())
                }
            } catch (e: SocketTimeoutException) {
                Log.d("ExploreFragment", "fetchBodiesEventsData timed out: ${e.message}")
            } catch (e: Exception) {
                Log.d("ExploreFragment", "Exception fetching Bodies/Events data", e)
            } finally {
                pbEvents.visibility = View.GONE
            }
        }
    }

    // Returns the currently selected body from the toggle button
    private fun getSelectedBody(): String {
        return if ((binding.bodyToggleButton as ToggleButton).isChecked) "sun" else "moon"
    }

    // Updates the RecyclerView adapter with all cards: two image cards and event cards.
    private fun updateCards(selectedBody: String) {
        val cardItems = mutableListOf<CardItem>()
        // Remove the image card addition from updateCards(), so only event cards are updated.
        combinedEvents?.let { combined ->
            val rows = if (selectedBody == "sun") {
                combined.sunEvents?.data?.table?.rows
            } else {
                combined.moonEvents?.data?.table?.rows
            }
            rows?.forEach { row ->
                row.cells.forEach { cell ->
                    cardItems.add(CardItem.EventCard(cell))
                }
            }
        }
        binding.eventsRecyclerView.adapter = MultiCardAdapter(cardItems)
    }

    // Handle permission request result
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                fetchMoonPhaseData()
                fetchStarChartData()
                fetchBodiesEventsData()
            } else {
                Log.d("ExploreFragment", "Location permission denied")
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}