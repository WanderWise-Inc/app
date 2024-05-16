package com.github.wanderwise_inc.app.viewmodel

import android.annotation.SuppressLint
import android.content.Context
import android.location.LocationManager
import android.os.Looper
import com.github.wanderwise_inc.app.model.location.Location
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch

/**
 * @param context the context used for retrieving system services and checking permissions.
 * @param client the FusedLocationProviderClient instance for requesting location updates.
 * @brief client responsible for retrieving user location updates using the Fused Location Provider
 *   API.
 */
class UserLocationClient(
    private val context: Context,
    private val client: FusedLocationProviderClient
) : LocationClient {

    /**
     * @param interval the interval, in milliseconds, at which location updates are requested.
     * @return a flow emitting the user's location updates.
     * @throws IllegalArgumentException if interval is not strictly positive
     * @throws LocationClient.LocationException if location permissions are denied or if there's no
     *   means of tracking position (e.g., GPS and network providers are disabled).
     * @brief requests location updates at a specified interval.
     */
    @SuppressLint("MissingPermission")
    override fun getLocationUpdates(interval: Long): Flow<Location> {
        if (interval <= 0) {
            throw IllegalArgumentException("Interval not strictly positive: interval=$interval")
        }

        return callbackFlow {
            if (!context.hasLocationPermission()) {
                throw LocationClient.LocationException("Location permissions denied")
            }

            val locationManager =
                context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
            val isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
            val isNetworkEnabled =
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
            if (!isGPSEnabled && !isNetworkEnabled) {
                throw LocationClient.LocationException("No mean of tracking position")
            }

            val request = LocationRequest.Builder(interval).build()

            val locationCallback =
                object : LocationCallback() {
                    override fun onLocationResult(result: LocationResult) {
                        super.onLocationResult(result)
                        result.locations.lastOrNull()?.let { androidLocation ->
                            launch { send(Location(androidLocation.latitude, androidLocation.longitude)) }
                        }
                    }
                }

            client.requestLocationUpdates(request, locationCallback, Looper.getMainLooper())

            awaitClose { client.removeLocationUpdates(locationCallback) }
        }
    }
}
