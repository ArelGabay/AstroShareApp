package com.example.astroshare.utils

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

/**
 * LocationHelper provides an easy way to retrieve the device's last known location using
 * Google's FusedLocationProviderClient. It leverages Kotlin coroutines to suspend execution
 * until the location is available, allowing for asynchronous location retrieval without blocking the main thread.
 */
class LocationHelper(private val context: Context) {

    // Initialize the FusedLocationProviderClient with the given context
    private val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

    /**
     * Retrieves the device's last known location.
     *
     * This method suspends the coroutine until the location is available or an error occurs.
     * It uses suspendCancellableCoroutine to allow the operation to be cancellable.
     *
     * @return the last known Location, or null if the location could not be retrieved.
     * @throws SecurityException if location permissions are not granted (hence the SuppressLint annotation).
     */
    @SuppressLint("MissingPermission") // Make sure location permission (ACCESS_FINE_LOCATION) is granted
    suspend fun getLastKnownLocation(): Location? =
        suspendCancellableCoroutine { cont ->
            // Request the last known location from the fused location provider
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location ->
                    // Resume the coroutine with the location result
                    cont.resume(location)
                }
                .addOnFailureListener {
                    // In case of failure, resume the coroutine with null
                    cont.resume(null)
                }
        }
}