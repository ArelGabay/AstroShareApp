package com.example.astroshare.data.repository

import com.example.astroshare.data.model.BodiesEventsResponse
import android.content.Context
import android.util.Log
import com.example.astroshare.data.remote.astronomy_api.getAuthorizationHeader
import com.example.astroshare.utils.LocationHelper
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.concurrent.TimeUnit

data class CombinedBodiesEventsResponse(
    val sunEvents: BodiesEventsResponse?,
    val moonEvents: BodiesEventsResponse?
)

class BodiesEventsRepository(private val context: Context) {

    private val locationHelper = LocationHelper(context)
    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()
    private val gson = Gson()

    /**
     * Calls the '/bodies/events/sun' AND '/bodies/events/moon' endpoints sequentially
     * using a GET request plus query params. Returns a CombinedBodiesEventsResponse with the
     * parsed results for each. If either fails, that field will be null.
     */
    suspend fun fetchBodiesEventsForSunAndMoon(): CombinedBodiesEventsResponse? = withContext(Dispatchers.IO) {
        // 1) Get location; if unavailable, return null
        val location = locationHelper.getLastKnownLocation() ?: run {
            Log.d("BodiesEventsRepository", "No location available")
            return@withContext null
        }

        // 2) Build dates/time
        val fromDate = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE)
        val toDate = LocalDate.now().plusDays(7).format(DateTimeFormatter.ISO_LOCAL_DATE)
        val time = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"))

        // 3) Elevation
        var elevation = 0.0
        if (location.hasAltitude()) {
            elevation = location.altitude
        }

        // Build full URLs with query params
        val sunUrl = "https://api.astronomyapi.com/api/v2/bodies/events/sun" +
                "?latitude=${location.latitude}" +
                "&longitude=${location.longitude}" +
                "&from_date=$fromDate" +
                "&to_date=$toDate" +
                "&time=$time" +
                "&elevation=$elevation"

        val moonUrl = "https://api.astronomyapi.com/api/v2/bodies/events/moon" +
                "?latitude=${location.latitude}" +
                "&longitude=${location.longitude}" +
                "&from_date=${fromDate}" +
                "&to_date=${toDate}" +
                "&time=$time" +
                "&elevation=$elevation"

        Log.d("BodiesEventsRepository", "Sun URL: $sunUrl")
        Log.d("BodiesEventsRepository", "Moon URL: $moonUrl")

        // Build GET requests
        val sunRequest = Request.Builder()
            .url(sunUrl)
            .get()
            .addHeader("Authorization", getAuthorizationHeader())
            .build()

        val moonRequest = Request.Builder()
            .url(moonUrl)
            .get()
            .addHeader("Authorization", getAuthorizationHeader())
            .build()

        // Execute each call
        val sunResponse = client.newCall(sunRequest).execute()
        val moonResponse = client.newCall(moonRequest).execute()

        // Log codes
        Log.d("BodiesEventsRepository", "Sun response code: ${sunResponse.code}")
        Log.d("BodiesEventsRepository", "Moon response code: ${moonResponse.code}")
        // Parse sun response
        val sunBodyString = if (sunResponse.isSuccessful) {
            sunResponse.body?.string()
        } else {
            Log.d("BodiesEventsRepository", "Sun call failed: ${sunResponse.code}")
            null
        }
        val sunEvents = sunBodyString?.let { bodyString ->
            Log.d("BodiesEventsRepository", "Sun JSON: $bodyString")
            gson.fromJson(bodyString, BodiesEventsResponse::class.java)
        }

        // Parse moon response
        val moonBodyString = if (moonResponse.isSuccessful) {
            moonResponse.body?.string()
        } else {
            Log.d("BodiesEventsRepository", "Moon call failed: ${moonResponse.code}")
            null
        }
        val moonEvents = moonBodyString?.let { bodyString ->
            Log.d("BodiesEventsRepository", "Moon JSON: $bodyString")
            gson.fromJson(bodyString, BodiesEventsResponse::class.java)
        }

        // Return combined
        return@withContext CombinedBodiesEventsResponse(
            sunEvents = sunEvents,
            moonEvents = moonEvents
        )
    }
}