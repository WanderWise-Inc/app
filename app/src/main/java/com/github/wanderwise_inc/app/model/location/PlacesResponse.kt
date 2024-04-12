

package com.github.wanderwise_inc.app.model.location

import com.google.android.gms.maps.model.LatLng

data class PlaceResponse(
    val geometry: Geometry,
    val name: String,
    val vicinity: String,
    val rating: Float
) {

    data class Geometry(
        val location: GeometryLocation
    )

    data class GeometryLocation(
        val lat: Double,
        val lng: Double
    )
}
/**
 * @return a Location object from a PlaceResponse object
 */
fun PlaceResponse.toLocation(): Location = Location(

    lat = geometry.location.lat,
    long = geometry.location.lng,
    title = name,
    address = vicinity,
    googleRating = rating
)
