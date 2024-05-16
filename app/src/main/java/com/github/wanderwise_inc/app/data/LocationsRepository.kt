package com.github.wanderwise_inc.app.data

import androidx.lifecycle.LiveData
import com.github.wanderwise_inc.app.model.location.Location

/** Handles interactions with geocode API */
interface LocationsRepository {
  fun getPlaces(
      name: String,
      limit: Int = 5,
      apiKey: String,
  ): LiveData<List<Location>?>
}
