package com.github.wanderwise_inc.app.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.github.wanderwise_inc.app.model.location.Location

class E2ELocationsRepository : LocationsRepository {
  override fun getPlaces(name: String, limit: Int, apiKey: String): LiveData<List<Location>?> {
    val places = MutableLiveData<List<Location>?>()
    places.value = listOf(
      Location(
        lat = 37.419000999999994,
        long = -122.08237596053958,
        title = "Google",
        address = "Mountain View, Santa Clara County, California, United States"
      )
    )
    return places
  }
}
