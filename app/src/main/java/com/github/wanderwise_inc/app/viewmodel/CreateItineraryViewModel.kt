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

const val TITLE_MAX_LENGTH = 80

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

  /** used to store how the itinerary is being created */
  var createItineraryManually: Boolean = false
  var createItineraryByTracking: Boolean = false

  fun getMaxTitleLength(): Int {
    return TITLE_MAX_LENGTH
  }

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

  /** uploads new itinerary and clears the builder */
  fun finishItinerary() {
    try {
      uploadNewItinerary()
    } catch (e: InvalidObjectException) {
      Log.d("CreateItineraryViewModel", "cannot complete a null itinerary. $e")
    }
    newItineraryBuilder = null
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

  /** @returns true if the title is valid * */
  fun validTitle(title: String): Boolean {
    return title.length < TITLE_MAX_LENGTH
  }

  fun invalidTitleMessage(): String {
    return "title field must be shorter than ${TITLE_MAX_LENGTH} characters"
  }

  /** Coroutine tasked with tracking user location */
  var locationJob: Job? = null
  val coroutineScope = CoroutineScope(Dispatchers.Main)

  /**
   * Adds device's current location to `newItineraryBuilder.locations` every `intervalMillis`
   * milliseconds
   */
  fun startLocationTracking(intervalMillis: Long, addLiveLocation: (Location) -> Unit) {
    locationJob?.cancel() // cancel some previous job
    locationJob =
        coroutineScope.launch {
          locationClient.getLocationUpdates(intervalMillis).collect { location ->
            newItineraryBuilder?.addLocation(location)
            addLiveLocation(location)
            Log.d("DEBUG_LIVE_LOCATION", "${newItineraryBuilder?.locations?.toList()}")
          }
        }
  }

  /** Stops the job tasked with tracking location (`locationJob`) */
  fun stopLocationTracking() {
    locationJob?.cancel()
  }

  public override fun onCleared() {
    super.onCleared()
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
