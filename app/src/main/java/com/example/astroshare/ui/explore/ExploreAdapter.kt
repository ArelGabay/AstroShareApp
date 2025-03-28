package com.example.astroshare.ui.explore

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.astroshare.data.model.BodyCells
import com.example.astroshare.databinding.ItemEventBinding
import com.example.astroshare.databinding.ItemImageCardBinding
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

fun formatEventTitle(rawTitle: String): String {
    return rawTitle.split("_").joinToString(" ") { word ->
        word.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
    }
}

fun formatDateTime(isoString: String): Pair<String, String> {
    // Parse the ISO 8601 string (e.g., "2025-03-28T16:02:59.000-04:00")
    val zonedDateTime = ZonedDateTime.parse(isoString)

    // Format the time as "4:02 PM"
    val timeFormatter = DateTimeFormatter.ofPattern("h:mm a")
    val formattedTime = zonedDateTime.format(timeFormatter)

    // Format the date as "March 28, 2025"
    val dateFormatter = DateTimeFormatter.ofPattern("MMMM d, yyyy")
    val formattedDate = zonedDateTime.format(dateFormatter)

    return Pair(formattedTime, formattedDate)
}
// Sealed class representing a card in the RecyclerView
sealed class CardItem {
    data class ImageCard(val cardType: CardType, val imageUrl: String) : CardItem()
    data class EventCard(val event: BodyCells) : CardItem()
}

enum class CardType {
    MOON_PHASE, STAR_CHART
}

class MultiCardAdapter(private val items: List<CardItem>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val VIEW_TYPE_IMAGE = 1
        private const val VIEW_TYPE_EVENT = 2
    }

    override fun getItemViewType(position: Int): Int {
        return when (items[position]) {
            is CardItem.ImageCard -> VIEW_TYPE_IMAGE
            is CardItem.EventCard -> VIEW_TYPE_EVENT
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_IMAGE -> {
                val binding = ItemImageCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                ImageCardViewHolder(binding)
            }
            else -> {
                val binding = ItemEventBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                EventCardViewHolder(binding)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val item = items[position]) {
            is CardItem.ImageCard -> (holder as ImageCardViewHolder).bind(item)
            is CardItem.EventCard -> (holder as EventCardViewHolder).bind(item.event)
        }
    }

    override fun getItemCount(): Int = items.size

    inner class ImageCardViewHolder(private val binding: ItemImageCardBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(card: CardItem.ImageCard) {
            binding.titleTextView.text = when(card.cardType) {
                CardType.MOON_PHASE -> "Moon Phase"
                CardType.STAR_CHART -> "Star Chart"
            }
            Glide.with(binding.root.context)
                .load(card.imageUrl)
                .into(binding.imageView)
        }
    }

    inner class EventCardViewHolder(private val binding: ItemEventBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(event: BodyCells) {
            // Format the rise and set times
            val (riseTime, _) = formatDateTime(event.rise)
            val (setTime, _) = formatDateTime(event.set)

            // For the event date, use the peak date if available, otherwise use the rise date
            val mainDateIso = event.eventHighlights?.peak?.date ?: event.rise
            val (_, formattedDate) = formatDateTime(mainDateIso)

            // Set the event type text, formatting it to replace underscores with spaces and capitalize
            binding.eventTypeTextView.text = formatEventTitle(event.type)
            // Set the friendly date
            binding.dateTextView.text = formattedDate
            // Set rise and set times
            binding.riseTextView.text = "Rise: $riseTime"
            binding.setTextView.text = "Set: $setTime"

            // Build a friendly details string for event highlights and extra info
            val details = StringBuilder()
            event.eventHighlights?.let { highlights ->
                highlights.partialStart?.let { ps ->
                    val (psTime, _) = formatDateTime(ps.date)
                    details.append("Partial Start: $psTime (Alt: ${ps.altitude})\n")
                }
                highlights.peak?.let { peak ->
                    val (peakTime, _) = formatDateTime(peak.date)
                    details.append("Peak: $peakTime (Alt: ${peak.altitude})\n")
                }
                highlights.partialEnd?.let { pe ->
                    val (peTime, _) = formatDateTime(pe.date)
                    details.append("Partial End: $peTime (Alt: ${pe.altitude})\n")
                }
            }
            event.extraInfo?.let { extra ->
                details.append("Obscuration: ${extra.obscuration}\n")
            }
            binding.detailsTextView.text = details.toString()
        }
    }
}