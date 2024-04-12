package com.github.wanderwise_inc.app.model.location

import com.google.android.gms.maps.model.LatLng

enum class LocationLabels(val dbLabel: String) {
  LAT("lat"),
  LONG("long"),
}

data class Location(
    val lat: Double,
    val long: Double,
) {
  fun toLatLng(): LatLng {
    return LatLng(lat, long)
  }

  /** @return a map representation of a location */
  fun toMap(): Map<String, Any> {
    return mapOf(LocationLabels.LAT.dbLabel to lat, LocationLabels.LONG.dbLabel to long)
  }

  companion object {
    fun fromLatLng(latLng: LatLng): Location {
      return Location(latLng.latitude, latLng.longitude)
    }
  }
}
