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

const val TITLE_MAX_LENGTH = 43

/**
 * ViewModel for creating a new Itinerary. Extends ItineraryViewModel and provides additional
 * functionality for the creation process of an Itinerary.
 */
class CreateItineraryViewModel(
    itineraryRepository: ItineraryRepository,
    directionsRepository: DirectionsRepository,
    locationsRepository: LocationsRepository,
    locationClient: LocationClient
) :
    ItineraryViewModel(
        itineraryRepository, directionsRepository, locationsRepository, locationClient) {

  /** Builder for the new itinerary that the signed-in user is currently building. */
  private var newItineraryBuilder: Itinerary.Builder? = null

  /** Flag indicating if the itinerary is being created manually. */
  var createItineraryManually: Boolean = false

  /** Flag indicating if the itinerary is being created by tracking the user's location. */
  var createItineraryByTracking: Boolean = false

  /**
   * Gets the maximum length allowed for the itinerary title.
   *
   * @return the maximum length for the title.
   */
  fun getMaxTitleLength(): Int {
    return TITLE_MAX_LENGTH
  }

  /**
   * Gets the itinerary currently being built by the user.
   *
   * @return the itinerary builder or `null` if not set.
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

  /**
   * Gets the current user ID from the itinerary builder.
   *
   * @return the user ID.
   * @throws InvalidObjectException if the itinerary builder is `null`.
   */
  fun getCurrentUid(): String {
    if (newItineraryBuilder == null) {
      throw InvalidObjectException("Itinerary.Builder is `null`")
    }
    return newItineraryBuilder!!.uid!!
  }

  /**
   * Initializes a new itinerary builder.
   *
   * @param userUid the user ID for the new itinerary.
   */
  fun startNewItinerary(userUid: String) {
    val newUid = getNewId()
    newItineraryBuilder = Itinerary.Builder(userUid = userUid, uid = newUid)
    Log.d(
        "CreateItineraryViewModel", "new itinerary created with uid: ${newItineraryBuilder!!.uid}")
  }

  /** Uploads the new itinerary and clears the builder. */
  fun finishItinerary() {
    try {
      uploadNewItinerary()
    } catch (e: InvalidObjectException) {
      Log.d("CreateItineraryViewModel", "cannot complete a null itinerary. $e")
    }
    newItineraryBuilder = null
  }

  /**
   * Uploads the new itinerary to the repository.
   *
   * @throws InvalidObjectException if the itinerary builder is `null`.
   */
  fun uploadNewItinerary() {
    if (newItineraryBuilder != null) itineraryRepository.setItinerary(newItineraryBuilder!!.build())
    else throw InvalidObjectException("Itinerary.Builder is `null`")
  }

  /**
   * Sets the title for the new itinerary.
   *
   * @param title the title to set.
   */
  fun setNewItineraryTitle(title: String) {
    newItineraryBuilder?.title = title
  }

  /**
   * Sets the description for the new itinerary.
   *
   * @param description the description to set.
   */
  fun setNewItineraryDescription(description: String) {
    newItineraryBuilder?.description = description
  }

  /**
   * Checks if the title is valid.
   *
   * @param title the title to check.
   * @return `true` if the title is valid, `false` otherwise.
   */
  fun validTitle(title: String): Boolean {
    return title.length < TITLE_MAX_LENGTH
  }

  /**
   * Gets the message to display when the title is invalid.
   *
   * @return the invalid title message.
   */
  fun invalidTitleMessage(): String {
    return "title field must be shorter than ${TITLE_MAX_LENGTH} characters"
  }

  /** Job for tracking user location. */
  var locationJob: Job? = null

  /** Coroutine scope for managing coroutines. */
  val coroutineScope = CoroutineScope(Dispatchers.Main)

  /**
   * Starts tracking the user's location and adds it to the itinerary at the specified interval.
   *
   * @param intervalMillis the interval in milliseconds to add locations.
   * @param addLiveLocation a callback function to handle live location updates.
   */
  fun startLocationTracking(intervalMillis: Long, addLiveLocation: (Location) -> Unit) {
    locationJob?.cancel() // cancel some previous job
    locationJob =
        coroutineScope.launch {
          locationClient.getLocationUpdates(intervalMillis).collect { location ->
            newItineraryBuilder?.addLocation(location)
            addLiveLocation(location)
            Log.d("CreateItineraryViewModel", "build = $newItineraryBuilder")
            Log.d("CreateItineraryViewModel", "locations = ${newItineraryBuilder?.locations}")
          }
        }
  }

  /** Stops the job tasked with tracking the location. */
  fun stopLocationTracking() {
    locationJob?.cancel()
  }

  /** Cleans up resources when the ViewModel is cleared. */
  public override fun onCleared() {
    super.onCleared()
    coroutineScope.cancel()
  }

  /**
   * Gets a list of itinerary fields that have not been set.
   *
   * @return a list of ItineraryLabels for the fields that have not been set.
   * @throws InvalidObjectException if the itinerary builder is `null`.
   */
  fun notSetValues(): List<String> {
    // look if all values have been set
    val notSetValues: MutableList<String> = mutableListOf()

    if (newItineraryBuilder == null) {
      // this case shouldn't be possible
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
