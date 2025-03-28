package com.example.astroshare.repository

import android.content.Context
import android.util.Log
import com.example.astroshare.data.model.StarChartObserver
import com.example.astroshare.data.model.StarChartParameters
import com.example.astroshare.data.model.StarChartView
import com.example.astroshare.data.model.StarChartRequest
import com.example.astroshare.data.model.StarChartResponse
import com.example.astroshare.data.remote.astronomy_api.getAuthorizationHeader
import com.example.astroshare.utils.LocationHelper
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.concurrent.TimeUnit


// Function to generate the Authorization header using credentials from BuildConfig.


class StarChartRepository(private val context: Context) {

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
    suspend fun fetchStarChart(): StarChartResponse? = withContext(Dispatchers.IO) {
        // Get current location. Return null if not available.
        val location = locationHelper.getLastKnownLocation() ?: return@withContext null

        val currentDate = LocalDate.now()
        val formattedDate = currentDate.format(DateTimeFormatter.ISO_LOCAL_DATE)

        // Build API request using current location.
        val requestObj = StarChartRequest(
            style = "default",
            observer = StarChartObserver(
                latitude = location.latitude,
                longitude = location.longitude,
                date = formattedDate // Update to a dynamic date if needed.
            ),
            view = StarChartView(
                type = "constellation",
                parameters = StarChartParameters(
                    constellation = "ori"
                )
            )
        )

        // Convert the request object to JSON.
        val jsonRequest = gson.toJson(requestObj)
        Log.d("StarChartRepository", "JSON Request: $jsonRequest")

        // Define media type for JSON.
        val mediaType = "application/json; charset=utf-8".toMediaType()
        // Create the RequestBody.
        val requestBody = jsonRequest.toRequestBody(mediaType)

        // Build the POST request with the Authorization header.
        val request = Request.Builder()
            .url("https://api.astronomyapi.com/api/v2/studio/star-chart")
            .post(requestBody)
            .addHeader("Authorization", getAuthorizationHeader())
            .addHeader("Content-Type", "application/json")
            .build()

        Log.d("StarChartRepository", "API request: $request")

        // Execute the request.
        val response = client.newCall(request).execute()
        Log.d("StarChartRepository", "API response code: ${response.code}")

        return@withContext if (response.isSuccessful) {
            // Read and log the response body.
            val jsonResponse = response.body?.string()
            Log.d("StarChartRepository", "Response JSON: $jsonResponse")
            // Convert JSON response to MoonPhaseResponse object.
            gson.fromJson(jsonResponse, StarChartResponse::class.java)
        } else {
            Log.d("StarChartRepository", "API call failed with code: ${response.code}")
            null
        }
    }
}