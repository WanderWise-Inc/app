package com.github.wanderwise_inc.app.model.location

import com.google.android.gms.maps.model.LatLng

data class Location(
    val lat: Double,
    val long: Double
) {
    fun toLatLng(): LatLng {
        return LatLng(lat, long)
    }

    companion object {
        fun fromLatLng(latLng: LatLng): Location {
            return Location(latLng.latitude, latLng.longitude)
        }
    }
}