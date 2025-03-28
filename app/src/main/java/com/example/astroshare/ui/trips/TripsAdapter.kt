package com.example.astroshare.ui.trips

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.astroshare.data.model.Trip
import com.example.astroshare.databinding.ItemTripBinding
import com.squareup.picasso.Picasso
import android.util.Log
import com.example.astroshare.R
import com.google.firebase.firestore.FirebaseFirestore

/**
 * Adapter for binding Trip objects to a RecyclerView.
 * It accepts the current user’s ID and a listener to handle edit and delete actions.
 */
class TripsAdapter(
    private val currentUserId: String,
    private val listener: TripItemListener
) : ListAdapter<Trip, TripsAdapter.TripViewHolder>(TripDiffCallback()) {

    interface TripItemListener {
        fun onEditTrip(trip: Trip)
        fun onDeleteTrip(trip: Trip)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TripViewHolder {
        val binding = ItemTripBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TripViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TripViewHolder, position: Int) {
        val trip = getItem(position)
        holder.bind(trip, currentUserId)
    }

    inner class TripViewHolder(private val binding: ItemTripBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(trip: Trip, currentUserId: String) {
            // Fetch uploader display name from Firestore
            FirebaseFirestore.getInstance().collection("users")
                .document(trip.ownerId)
                .get()
                .addOnSuccessListener { document ->
                    val displayName = document.getString("displayName") ?: "Unknown"
                    binding.tvUploadedBy.text = "$displayName"
                }
                .addOnFailureListener {
                    binding.tvUploadedBy.text = "Uploaded by: Unknown"
                }

            binding.tvTripTitle.text = "${trip.title}"
            binding.tvTripContent.text = "${trip.content}"
            binding.tvTripLocation.text = "${trip.locationName}"

            // Load image
            if (!trip.imageUrl.isNullOrEmpty()) {
                Picasso.get()
                    .load(trip.imageUrl)
                    .placeholder(R.drawable.image_loading)
                    .error(R.drawable.image_loading)
                    .into(binding.ivTripImage)
            } else {
                binding.ivTripImage.setImageResource(R.drawable.image_loading)
            }

            // Show edit/delete buttons only if the trip's owner matches the current user.
            if (trip.ownerId == currentUserId) {
                binding.btnEditTrip.visibility = View.VISIBLE
                binding.btnDeleteTrip.visibility = View.VISIBLE
            } else {
                binding.btnEditTrip.visibility = View.GONE
                binding.btnDeleteTrip.visibility = View.GONE
            }

            binding.btnEditTrip.setOnClickListener { listener.onEditTrip(trip) }
            binding.btnDeleteTrip.setOnClickListener { listener.onDeleteTrip(trip) }
        }
    }
}

/**
 * DiffUtil callback for optimizing RecyclerView updates.
 */
class TripDiffCallback : DiffUtil.ItemCallback<Trip>() {
    override fun areItemsTheSame(oldItem: Trip, newItem: Trip): Boolean {
        return oldItem.id == newItem.id
    }
    override fun areContentsTheSame(oldItem: Trip, newItem: Trip): Boolean {
        return oldItem == newItem
    }
}