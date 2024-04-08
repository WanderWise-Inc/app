package com.github.wanderwise_inc.app.model.location

import com.google.android.gms.maps.model.LatLng
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

enum class LocationLabels(val dbLabel: String) {
    LAT("lat"),
    LONG("long"),
}

typealias Kilometers = Double

data class Location(
    val lat: Double,
    val long: Double,
) {
    fun toLatLng(): LatLng {
        return LatLng(lat, long)
    }

    /**
     * @return a map representation of a location
     */
    fun toMap(): Map<String, Any> {
        return mapOf(
            LocationLabels.LAT.dbLabel to lat,
            LocationLabels.LONG.dbLabel to long
        )
    }

    /**
     * @return the distance in kilometers to another point
     */
    private fun distTo(other: Location): Kilometers {
        if (other.lat == lat && other.long == long)
            return 0.0

        val earthRadius: Kilometers = 6371.0

        // Convert latitude and longitude from degrees to radians
        val lat1Rad = Math.toRadians(this.lat)
        val lon1Rad = Math.toRadians(this.long)
        val lat2Rad = Math.toRadians(other.lat)
        val lon2Rad = Math.toRadians(other.long)

        // Calculate differences in latitude and longitude
        val dLat = lat2Rad - lat1Rad
        val dLon = lon2Rad - lon1Rad

        // Calculate the square of half the chord length between the points
        val a = sin(dLat / 2).pow(2) + cos(lat1Rad) * cos(lat2Rad) * sin(dLon / 2).pow(2)

        // Calculate the angular distance in radians
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))

        return earthRadius * c
    }

    /**
     * @return the distance to the start of an itinerary
     */
    fun distToStart(itinerary: Itinerary): Kilometers {
        return this.distTo(itinerary.locations[0])
    }

    companion object {
        fun fromLatLng(latLng: LatLng): Location {
            return Location(latLng.latitude, latLng.longitude)
        }
    }
}