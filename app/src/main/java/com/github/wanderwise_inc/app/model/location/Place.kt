
package com.github.wanderwise_inc.app.model.location

import com.google.android.gms.maps.model.LatLng

data class Place(
    val name: String,
    val latLng: Location, //location
    val address: String,
    val rating: Float
)