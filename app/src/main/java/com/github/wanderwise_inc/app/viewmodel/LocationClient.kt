package com.github.wanderwise_inc.app.viewmodel

import android.location.Location
import kotlinx.coroutines.flow.Flow

interface LocationClient {

    /**
     * @brief get the updated location
     *
     * @param interval the frequency of the updates in ms
     * @return a flow containing the updated locations
     */
    fun getLocationUpdates(interval: Long): Flow<Location>

    /**
     * @brief exception class for error with the LocationClient
     *
     * @param message the description of the exception
     */
    class LocationException(message: String): Exception()
}