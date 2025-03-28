package com.example.astroshare.data.model

data class MoonPhaseRequest(
    val format: String,
    val style: MoonStyle,
    val observer: MoonObserver,
    val view: MoonView
)

data class MoonStyle(
    val moonStyle: String,
    val backgroundStyle: String,
    val backgroundColor: String,
    val headingColor: String,
    val textColor: String
)

data class MoonObserver(
    val latitude: Double,
    val longitude: Double,
    val date: String
)

data class MoonView(
    val type: String,
    val orientation: String
)

data class MoonPhaseResponse(
    val data: MoonPhaseData
)

data class MoonPhaseData(
    val imageUrl: String
)
