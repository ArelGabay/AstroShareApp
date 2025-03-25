package com.example.astroshare.ui.trips

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.astroshare.data.model.Trip
import com.example.astroshare.databinding.ItemTripBinding
import com.squareup.picasso.Picasso
import android.util.Log

class TripsAdapter : ListAdapter<Trip, TripsAdapter.TripViewHolder>(TripDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TripViewHolder {
        val binding = ItemTripBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TripViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TripViewHolder, position: Int) {
        val trip = getItem(position)
        holder.bind(trip)
    }

    inner class TripViewHolder(private val binding: ItemTripBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(trip: Trip) {
            Log.d("TripsAdapter", "Binding: ${trip.title}, content=${trip.content}")
            binding.tvTripTitle.text = trip.title
            binding.tvTripLocation.text = trip.locationName
            binding.tvTripContent.text = trip.content
            if (!trip.imageUrl.isNullOrEmpty()) {
                // Load the trip image using Picasso; use a placeholder drawable while loading
                Picasso.get()
                    .load(trip.imageUrl)
                    .placeholder(com.example.astroshare.R.drawable.image_loading) // add your placeholder drawable here
                    .error(com.example.astroshare.R.drawable.image_loading)
                    .into(binding.ivTripImage)
            } else {
                binding.ivTripImage.setImageResource(com.example.astroshare.R.drawable.image_loading)
            }
        }
    }
}

class TripDiffCallback : DiffUtil.ItemCallback<Trip>() {
    override fun areItemsTheSame(oldItem: Trip, newItem: Trip): Boolean {
        return oldItem.id == newItem.id
    }
    override fun areContentsTheSame(oldItem: Trip, newItem: Trip): Boolean {
        return oldItem == newItem
    }
}