package com.github.wanderwise_inc.app.viewmodel

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
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

private const val DEBUG_TAG: String = "MAP_VIEWMODEL"
/** @brief ViewModel class for providing `Location`s and `Itinerary`s to the map UI */
class MapViewModel(
    private val itineraryRepository: ItineraryRepository,
    private val directionsRepository: DirectionsRepository
) : ViewModel() {
  /** @return a flow of all public itineraries */
  fun getAllPublicItineraries(): Flow<List<Itinerary>> {
    return itineraryRepository.getPublicItineraries()
  }

  /** @return a flow of all `Itinerary`s associated to the currently logged in user */
  fun getUserItineraries(userUid: String): Flow<List<Itinerary>> {
    return itineraryRepository.getUserItineraries(userUid)
  }

  /** @return the total number of likes from a list of itineraries */
  fun getItineraryUpvotes(itineraries: List<Itinerary>): Int {
    var totalLikes: Int = 0
    for (itinerary in itineraries) totalLikes += itinerary.numLikes
    return totalLikes
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

  /** @brief sets an itinerary in DB */
  fun setItinerary(itinerary: Itinerary) {
    itineraryRepository.setItinerary(itinerary)
  }

  /** @brief deletes an itinerary from the database */
  fun deleteItinerary(itinerary: Itinerary) {
    itineraryRepository.deleteItinerary(itinerary)
  }

  private val _polylinePointsLiveData = MutableLiveData<List<LatLng>>()
  val polylinePointsLiveData: LiveData<List<LatLng>> = _polylinePointsLiveData // gettable from view

  /**
   * fetches Polyline points encoded as `LatLng` and updates a `LiveData` variable observed by some
   * composable function
   */
  fun fetchPolylineLocations(itinerary: Itinerary) {
    assert(itinerary.locations.size >= 2)
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
              waypoints = waypoints.toTypedArray())
          .observeForever { response -> _polylinePointsLiveData.value = response ?: listOf() }
    }
  }
}
