package com.github.wanderwise_inc.app.viewmodel

import com.github.wanderwise_inc.app.model.location.Location
import kotlinx.coroutines.flow.Flow

interface LocationClient {

  /**
   * @param interval the frequency of the updates in ms
   * @return a flow containing the updated locations
   * @brief get the updated location
   */
  fun getLocationUpdates(interval: Long): Flow<Location>

  /**
   * @param message the description of the exception
   * @brief exception class for error with the LocationClient
   */
  class LocationException(message: String) : Exception()
}
