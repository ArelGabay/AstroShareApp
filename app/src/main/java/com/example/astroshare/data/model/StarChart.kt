package com.example.astroshare.data.model

data class StarChartRequest(
    val style: String,
    val observer: StarChartObserver,
    val view: StarChartView
)

data class StarChartObserver(
    val latitude: Double,
    val longitude: Double,
    val date: String
)

data class StarChartView(
    val type: String,
    val parameters: StarChartParameters,
)

data class StarChartParameters(
    val constellation: String,
)

data class StarChartResponse(
    val data: StarChartData
)

data class StarChartData(
    val imageUrl: String
)
