package com.example.astroshare.data.repository

import android.content.Context
import com.example.astroshare.data.model.MoonPhaseRequest
import com.example.astroshare.data.model.MoonPhaseResponse
import com.example.astroshare.data.model.MoonObserver
import com.example.astroshare.data.model.MoonStyle
import com.example.astroshare.data.model.MoonView
import com.example.astroshare.utils.LocationHelper
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import com.example.astroshare.data.remote.astronomy_api.getAuthorizationHeader
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.concurrent.TimeUnit
import android.util.Log

class MoonPhaseRepository(private val context: Context) {

    // Instance to obtain device location.
    private val locationHelper = LocationHelper(context)
    // OkHttpClient instance.
    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()
    // Gson instance for JSON conversion.
    private val gson = Gson()

    // Fetch moon phase data from the API.
    suspend fun fetchMoonPhase(): MoonPhaseResponse? = withContext(Dispatchers.IO) {
        // Get current location. Return null if not available.
        val location = locationHelper.getLastKnownLocation() ?: return@withContext null
        Log.d("MoonPhaseRepository", "Location: $location")
        val currentDate = LocalDate.now()
        val formattedDate = currentDate.format(DateTimeFormatter.ISO_LOCAL_DATE)

        // Build API request using current location.
        val requestObj = MoonPhaseRequest(
            format = "png",
            style = MoonStyle(
                moonStyle = "default",
                backgroundStyle = "stars",
                backgroundColor = "#000000",
                headingColor = "#ffffff",
                textColor = "#ffffff"
            ),
            observer = MoonObserver(
                latitude = location.latitude,
                longitude = location.longitude,
                date = formattedDate// Update to a dynamic date if needed.
            ),
            view = MoonView(
                type = "landscape-simple",
                orientation = "north-up"
            )
        )

        // Convert the request object to JSON.
        val jsonRequest = gson.toJson(requestObj)
        Log.d("MoonPhaseRepository", "JSON Request: $jsonRequest")

        // Define media type for JSON.
        val mediaType = "application/json; charset=utf-8".toMediaType()
        // Create the RequestBody.
        val requestBody = jsonRequest.toRequestBody(mediaType)

        // Build the POST request with the Authorization header.
        val request = Request.Builder()
            .url("https://api.astronomyapi.com/api/v2/studio/moon-phase")
            .post(requestBody)
            .addHeader("Authorization", getAuthorizationHeader())
            .addHeader("Content-Type", "application/json")
            .build()

        Log.d("MoonPhaseRepository", "API request: $request")

        // Execute the request.
        val response = client.newCall(request).execute()
        Log.d("MoonPhaseRepository", "API response code: ${response.code}")

        return@withContext if (response.isSuccessful) {
            // Read and log the response body.
            val jsonResponse = response.body?.string()
            Log.d("MoonPhaseRepository", "Response JSON: $jsonResponse")
            // Convert JSON response to MoonPhaseResponse object.
            gson.fromJson(jsonResponse, MoonPhaseResponse::class.java)
        } else {
            Log.d("MoonPhaseRepository", "API call failed with code: ${response.code}")
            null
        }
    }
}