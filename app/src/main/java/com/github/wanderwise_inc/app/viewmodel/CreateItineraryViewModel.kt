package com.github.wanderwise_inc.app.viewmodel

import android.util.Log
import com.github.wanderwise_inc.app.data.DirectionsRepository
import com.github.wanderwise_inc.app.data.ItineraryRepository
import com.github.wanderwise_inc.app.data.LocationsRepository
import com.github.wanderwise_inc.app.model.location.Itinerary
import com.github.wanderwise_inc.app.model.location.ItineraryLabels
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
    private val locationsRepository: LocationsRepository,
    private val locationClient: LocationClient
) :
    ItineraryViewModel(
        itineraryRepository, directionsRepository, locationsRepository, locationClient) {
  /** New itinerary that the signed in user is currently building */
  private var newItineraryBuilder: Itinerary.Builder? = null

  /**
   * @return Itinerary being built by the user currently. The composable is responsible for setting
   *   it to `null` when the creation is finished
   *
   * **USAGE EXAMPLE**
   *
   * ```
   * val newItineraryBuilder = mapViewModel.getNewItinerary()!!
   * // any attributes that should cause a recomposition should be remembered
   * var title by remember {
   *  mutableStateOf(newItinerary.title)
   * }
   * Button (
   *  onClick = {
   *    title = newTitle                        // update mutableState for recomposition
   *    newItineraryBuilder.addTitle(newTitle)  // update shared state across screens
   *  }
   * )
   * ```
   */
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
  /** @returns a list of ItineraryLabels of fields that haven't been set * */
  fun notSetValues(): List<String> {
    // look if all values have been set
    val notSetValues: MutableList<String> = mutableListOf()

    if (newItineraryBuilder == null) {
      // this case shouldnt be possible
      // add error message
      throw InvalidObjectException("Itinerary.Builder is `null`")
    } else {
      if (newItineraryBuilder!!.price == null) {
        notSetValues.add(ItineraryLabels.PRICE)
      }
      if (newItineraryBuilder!!.time == null) {
        notSetValues.add(ItineraryLabels.TIME)
      }
      if (newItineraryBuilder!!.description == null) {
        notSetValues.add(ItineraryLabels.DESCRIPTION)
      }
      if (newItineraryBuilder!!.title == null) {
        notSetValues.add(ItineraryLabels.TITLE)
      }
      if (newItineraryBuilder!!.locations.isEmpty()) {
        notSetValues.add(ItineraryLabels.LOCATIONS)
      }
      if (newItineraryBuilder!!.tags.isEmpty()) {
        notSetValues.add(ItineraryLabels.TAGS)
      }
    }

    // return the notSetValues (empty if everything has been set)
    return notSetValues
  }
}
