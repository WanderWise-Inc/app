package com.github.wanderwise_inc.app.model.location

import com.google.android.gms.maps.model.LatLng
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

object LocationLabels {
    const val LAT = "lat"
    const val LONG = "long"
    const val TITLE = "title"
    const val ADDRESS = "address"
    const val GOOGLE_RATING = "google_rating"
}

typealias Kilometers = Double

/**
 * @brief represents a location
 */
data class Location(
    val lat: Double,
    val long: Double,
    val title: String? = null,
    val address: String? = null,
    val googleRating: Float? = null,
) {
    fun toLatLng(): LatLng {
        return LatLng(lat, long)
    }

    /**
     * @return a map representation of a location
     */
    fun toMap(): Map<String, Any> {
        return mapOf(
            LocationLabels.LAT to lat,
            LocationLabels.LONG to long,
            LocationLabels.TITLE to (title ?: ""),
            LocationLabels.ADDRESS to (address ?: ""),
            LocationLabels.GOOGLE_RATING to (googleRating ?: 0f)
        )
    }

    /**
     * @return the distance in kilometers to another point
     */
    fun distTo(other: Location): Kilometers {
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

        println("dist=${earthRadius*c}")
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

    /**
     * @brief no-argument constructor for firebase de-serialization
     */
    constructor() : this (0.0, 0.0)
}