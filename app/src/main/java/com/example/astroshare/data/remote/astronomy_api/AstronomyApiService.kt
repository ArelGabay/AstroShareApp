package com.example.astroshare.data.remote.astronomy_api

import com.example.astroshare.data.model.BodiesEventsResponse
import com.example.astroshare.BuildConfig
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import com.example.astroshare.data.model.MoonPhaseRequest
import com.example.astroshare.data.model.MoonPhaseResponse
import com.example.astroshare.data.model.StarChartRequest
import com.example.astroshare.data.model.StarChartResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import android.util.Log

fun getAuthorizationHeader(): String {
    Log.d("AstronomyAPI", "Generating Authorization header")
    // Retrieve credentials from BuildConfig (injected from local.properties)
    var credentials = BuildConfig.ASTRONOMY_API_AFTER_HASH
    Log.d("AstronomyAPI", "Credentials: $credentials")
//    val encodedCredentials = Base64.encodeToString(credentials.toByteArray(Charsets.UTF_8), Base64.NO_WRAP)
    return credentials
}

interface AstronomyApiService {
    @POST("studio/moon-phase")
    suspend fun getMoonPhase(
        @Body request: MoonPhaseRequest
    ): Response<MoonPhaseResponse>

    @POST("studio/star-chart")
    suspend fun getStarChart(
        @Body request: StarChartRequest
    ): Response<StarChartResponse>

    @GET("bodies/events/{bodyId}")
    suspend fun getEvents(
        @Path("bodyId") bodyId: String,
        @Query("latitude") latitude: Double,
        @Query("longitude") longitude: Double,
        @Query("from_date") fromDate: String,
        @Query("to_date") toDate: String,
        @Query("time") time: String,
        @Query("elevation") elevation: Double
    ): Response<BodiesEventsResponse>
}