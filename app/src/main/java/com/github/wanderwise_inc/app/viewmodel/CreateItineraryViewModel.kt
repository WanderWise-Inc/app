package com.github.wanderwise_inc.app.viewmodel

import android.util.Log
import com.github.wanderwise_inc.app.data.DirectionsRepository
import com.github.wanderwise_inc.app.data.ItineraryRepository
import com.github.wanderwise_inc.app.model.location.Itinerary
import com.github.wanderwise_inc.app.model.location.Location
import java.io.InvalidObjectException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

class CreateItineraryViewModel(
    private val itineraryRepository: ItineraryRepository,
    private val directionsRepository: DirectionsRepository,
    private val locationClient: LocationClient
) : ItineraryViewModel(itineraryRepository, directionsRepository, locationClient) {
  /** New itinerary that the signed in user is currently building */
  private var newItineraryBuilder: Itinerary.Builder? = null

  fun getNewItinerary(): Itinerary.Builder? {
    return newItineraryBuilder
  }

  /** initializes a new `MutableItinerary` */
  fun startNewItinerary(userUid: String) {
    newItineraryBuilder = Itinerary.Builder(userUid = userUid)
  }

  fun uploadNewItinerary() {
    if (newItineraryBuilder != null) itineraryRepository.setItinerary(newItineraryBuilder!!.build())
    else throw InvalidObjectException("Itinerary.Builder is `null`")
  }

  fun setNewItineraryTitle(title: String) {
    newItineraryBuilder?.title = title
  }

  fun setNewItineraryDescription(description: String) {
    newItineraryBuilder?.description = description
  }

  /** Coroutine tasked with tracking user location */
  private var locationJob: Job? = null
  private val coroutineScope = CoroutineScope(Dispatchers.Main)

  /**
   * Adds device's current location to `newItineraryBuilder.locations` every `intervalMillis`
   * milliseconds
   */
  fun startLocationTracking(intervalMillis: Long) {
    locationJob?.cancel() // cancel some previous job
    locationJob =
        coroutineScope.launch {
          locationClient.getLocationUpdates(intervalMillis).collect { androidLocation ->
            val currLocation = Location(androidLocation.latitude, androidLocation.longitude)
            newItineraryBuilder?.addLocation(currLocation)
            Log.d(
                "Location tracking",
                "adding (${androidLocation.latitude}, ${androidLocation.longitude})")
            Log.d(
                "Location tracking",
                "builder locations = ${newItineraryBuilder?.locations ?: emptyList()}")
          }
        }
  }

  /** Stops the job tasked with tracking location (`locationJob`) */
  fun stopLocationTracking() {
    Log.d("Location tracking", "stopping location tracking")
    locationJob?.cancel()
  }

  override fun onCleared() {
    super.onCleared()
    Log.d("CreateItineraryViewModel", "cancelling coroutine")
    coroutineScope.cancel()
  }
}
