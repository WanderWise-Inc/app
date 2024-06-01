package com.github.wanderwise_inc.app.viewmodel

import com.github.wanderwise_inc.app.model.location.Location
import kotlinx.coroutines.flow.Flow

/**
 * `LocationClient` is an interface that defines the contract for classes that provide location.
 *
 * This interface should be implemented by classes that provide location updates.
 */
interface LocationClient {

    /**
     * Gets location updates at the specified interval.
     *
     * This method returns a Flow that emits Location objects.
     * Each Location object represents the device's location at a particular point in time.
     * The updates are emitted at the interval specified by the `interval` parameter.
     *
     * @param interval The interval at which location updates are desired, in milliseconds.
     * @return A Flow that emits Location objects at the specified interval.
     */
    fun getLocationUpdates(interval: Long): Flow<Location>

    /**
     * `LocationException` is a class that represents exceptions related to location updates.
     *
     * This class extends the `Exception` class and is used to indicate that an error occurred while getting location updates.
     *
     * @param message The detail message string of this throwable.
     */
    class LocationException(message: String) : Exception()
}
