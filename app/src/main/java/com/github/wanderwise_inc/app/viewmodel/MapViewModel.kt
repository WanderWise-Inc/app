package com.github.wanderwise_inc.app.viewmodel

import android.location.Location
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.wanderwise_inc.app.BuildConfig
import com.github.wanderwise_inc.app.data.DirectionsRepository
import com.github.wanderwise_inc.app.data.ItineraryRepository
import com.github.wanderwise_inc.app.model.location.Itinerary
import com.github.wanderwise_inc.app.model.location.ItineraryPreferences
import com.google.android.gms.maps.model.LatLng
import java.io.InvalidObjectException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch

private const val DEBUG_TAG: String = "MAP_VIEWMODEL"
/** @brief ViewModel class for providing `Location`s and `Itinerary`s to the map UI */
class MapViewModel(
    private val itineraryRepository: ItineraryRepository,
    private val directionsRepository: DirectionsRepository,
    private val userLocationClient: UserLocationClient,
) : ViewModel() {
  private var focusedItinerary: Itinerary? = null

  /** New itinerary that the signed in user is currently building */
  private var newItineraryBuilder: Itinerary.Builder? = null

  /** @return the itinerary the user has clicked on */
  fun getFocusedItinerary(): Itinerary? {
    return focusedItinerary
  }

  /** changes the focused itinerary to a new one */
  fun setFocusedItinerary(itinerary: Itinerary?) {
    focusedItinerary = itinerary
  }

  /** @return a flow of all public itineraries */
  fun getAllPublicItineraries(): Flow<List<Itinerary>> {
    return itineraryRepository.getPublicItineraries()
  }

  /** @return a flow of all `Itinerary`s associated to the currently logged in user */
  fun getUserItineraries(userUid: String): Flow<List<Itinerary>> {
    return itineraryRepository.getUserItineraries(userUid)
  }

  /** @return the total number of likes from a list of itineraries */
  fun getItineraryLikes(itineraries: List<Itinerary>): Int {

    var totalLikes: Int = 0

    for (itinerary in itineraries) totalLikes += itinerary.numLikes
    return totalLikes
  }

  /** @brief increment likes of a given itinerary */
  fun incrementItineraryLikes(itinerary: Itinerary) {
    itinerary.numLikes++
    itineraryRepository.updateItinerary(itinerary.uid, itinerary)
  }

  /** @brief decrement likes of a given itinerary */
  fun decrementItineraryLikes(itinerary: Itinerary) {
    itinerary.numLikes--
    itineraryRepository.updateItinerary(itinerary.uid, itinerary)
  }

  /**
   * @param preferences user query preferences
   * @return a list of itineraries matching a user's query preferences
   * @see Itinerary.scoreFromPreferences for sorting the list from View
   */
  fun getItinerariesFromPreferences(preferences: ItineraryPreferences): Flow<List<Itinerary>> {
    return itineraryRepository.getItinerariesWithTags(preferences.tags)
  }

  /** @return a sorted list of itineraries scored based on preferences */
  fun sortItinerariesFromPreferences(
      itineraries: List<Itinerary>,
      preferences: ItineraryPreferences
  ): List<Itinerary> {
    // invert the sign so that the list is sorted in descending order
    return itineraries.sortedBy { -it.scoreFromPreferences(preferences) }
  }

  /** @return list of itineraries queried based on their UIDs */
  fun getItineraryFromUids(uidList: List<String>): Flow<List<Itinerary>> {
    return flow {
      val result = uidList.map { uid -> itineraryRepository.getItinerary(uid) }
      emit(result)
    }
  }

  /** @brief sets an itinerary in DB */
  fun setItinerary(itinerary: Itinerary) {
    itineraryRepository.setItinerary(itinerary)
  }

  /** @brief deletes an itinerary from the database */
  fun deleteItinerary(itinerary: Itinerary) {
    itineraryRepository.deleteItinerary(itinerary)
  }

  private val _polylinePointsLiveData = MutableLiveData<List<LatLng>>()
  private val polylinePointsLiveData: LiveData<List<LatLng>> =
      _polylinePointsLiveData // gettable from view
  /**
   * fetches Polyline points encoded as `LatLng` and updates a `LiveData` variable observed by some
   * composable function
   */
  fun fetchPolylineLocations(itinerary: Itinerary) {
    Log.d(DEBUG_TAG, "called fetch polyline points")
    if (itinerary.locations.size < 2)
      return

    val origin = itinerary.locations.first()
    val destination = itinerary.locations.last()
    val originEncoded = "${origin.lat}, ${origin.long}"
    val destinationEncoded = "${destination.lat}, ${destination.long}"

    val waypoints = itinerary.locations.drop(1).dropLast(1).map { "${it.lat},${it.long}" }

    val key = BuildConfig.MAPS_API_KEY
    viewModelScope.launch {
      directionsRepository
          .getPolylineWayPoints(
              origin = originEncoded,
              destination = destinationEncoded,
              apiKey = key,
              waypoints = waypoints)
          .observeForever { response -> _polylinePointsLiveData.value = response ?: listOf() }
    }
  }

  fun getPolylinePointsLiveData(): LiveData<List<LatLng>> {
    return polylinePointsLiveData
  }

  /** @brief get a Flow of the user location updated every second */
  fun getUserLocation(): Flow<Location> {
    return userLocationClient.getLocationUpdates(1000)
  }

  /**
   * @return Itinerary being built by the user currently. The composable is responsible for setting
   *   it to `null` when the creation is finished
   *
   * If there is no itinerary currently being created, initializes a new one with the provided
   * `userUid`
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

  /* fun filterItinerariesByPrice(priceRange: FloatRange): List<Itinerary> {
    return allItineraries.filter { it.price in priceRange }
  }*/
}
