// data/model/Trip.kt
package com.example.astroshare.data.model

data class Trip(
    val id: String = "",
    val ownerId: String = "",
    val title: String = "",
    val content: String = "",
    val imageUrl: String? = null,
    val timestamp: Long = 0L,
    val locationName: String = "",
    val locationDetails: String = ""
)