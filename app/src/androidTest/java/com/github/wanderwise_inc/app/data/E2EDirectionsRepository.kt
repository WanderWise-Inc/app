package com.github.wanderwise_inc.app.data

import androidx.lifecycle.LiveData
import com.google.android.gms.maps.model.LatLng

class E2EDirectionsRepository : DirectionsRepository {
  override fun getPolylineWayPoints(
      origin: String,
      destination: String,
      waypoints: List<String>,
      apiKey: String
  ): LiveData<List<LatLng>?> {
    TODO("Not yet implemented")
  }
}
