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
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch

/**
 * `UserLocationClient` is a class that provides location updates.
 *
 * This class implements the `LocationClient` interface and uses the `FusedLocationProviderClient` to get location updates.
 *
 * @property context The context in which the `UserLocationClient` is being used.
 * @property client The `FusedLocationProviderClient` used to get location updates.
 * @constructor Creates a `UserLocationClient` with the specified context and client.

 */
class UserLocationClient(
    private val context: Context,
    private val client: FusedLocationProviderClient
) : LocationClient {

    /**
     * Gets location updates at the specified interval.
     *
     * This method returns a Flow that emits Location objects. Each Location object represents the device's location at a particular point in time.
     * The updates are emitted at the interval specified by the `interval` parameter.
     *
     * @param interval The interval at which location updates are desired, in milliseconds.
     * @return A Flow that emits Location objects at the specified interval.
     * @throws IllegalArgumentException If the interval is not strictly positive.
     * @throws LocationClient.LocationException If location permissions are denied or if there is no mean of tracking position.
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
                            launch {
                                send(
                                    Location.placeLocation(
                                        LatLng(androidLocation.latitude, androidLocation.longitude)
                                    )
                                )
                            }
                        }
                    }
                }

            client.requestLocationUpdates(request, locationCallback, Looper.getMainLooper())

            awaitClose { client.removeLocationUpdates(locationCallback) }
        }
    }
}
