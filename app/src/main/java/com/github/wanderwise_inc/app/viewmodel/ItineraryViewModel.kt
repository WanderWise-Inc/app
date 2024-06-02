package com.github.wanderwise_inc.app.viewmodel

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.github.wanderwise_inc.app.BuildConfig
import com.github.wanderwise_inc.app.data.DirectionsRepository
import com.github.wanderwise_inc.app.data.ItineraryRepository
import com.github.wanderwise_inc.app.data.LocationsRepository
import com.github.wanderwise_inc.app.model.location.Itinerary
import com.github.wanderwise_inc.app.model.location.ItineraryPreferences
import com.github.wanderwise_inc.app.model.location.Location
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch

private const val DEBUG_TAG: String = "ITINERARY_VIEWMODEL"

/** @brief ViewModel class for providing `Location`s and `Itinerary`s and related logic */
open class ItineraryViewModel(
    protected val itineraryRepository: ItineraryRepository,
    protected val directionsRepository: DirectionsRepository,
    protected val locationsRepository: LocationsRepository,
    protected val locationClient: LocationClient,
) : ViewModel() {
  private var focusedItinerary: Itinerary? = null

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
    val ret = itineraryRepository.getUserItineraries(userUid)
    return ret
  }

  /** @return the total number of likes from a list of itineraries */
  fun getItineraryLikes(itineraries: List<Itinerary>): Int {

    var totalLikes = 0

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
      val result = uidList.mapNotNull { uid -> itineraryRepository.getItinerary(uid) }
      Log.d("ItineraryViewModel", "emitting $result")
      emit(result)
    }
  }

  /** saves a list of `Itinerary` to persistent storage */
  fun saveItineraries(itineraries: List<Itinerary>) {
    viewModelScope.launch { itineraryRepository.writeItinerariesToDisk(itineraries) }
  }

  /** saves an `Itinerary` to persistent storage */
  fun saveItinerary(itinerary: Itinerary) {
    viewModelScope.launch { itineraryRepository.writeItinerariesToDisk(listOf(itinerary)) }
  }

  /** @brief sets an itinerary in DB */
  fun setItinerary(itinerary: Itinerary) {
    viewModelScope.launch { itineraryRepository.setItinerary(itinerary) }
  }

  /** @brief deletes an itinerary from the database */
  fun deleteItinerary(itinerary: Itinerary) {
    itineraryRepository.deleteItinerary(itinerary)
  }

  fun getNewId(): String {
    return itineraryRepository.getNewId()
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
    if (itinerary.locations.size < 2) return

    val origin = itinerary.locations.first()
    val destination = itinerary.locations.last()
    val originEncoded = "${origin.lat}, ${origin.long}"
    val destinationEncoded = "${destination.lat}, ${destination.long}"

    val waypoints = itinerary.locations.drop(1).dropLast(1).map { "${it.lat},${it.long}" }

    val key = BuildConfig.MAPS_API_KEY
    viewModelScope.launch {
      Log.d(
          DEBUG_TAG,
          "origin:$originEncoded :: destination:$destinationEncoded :: waypoints:$waypoints")
      directionsRepository
          .getPolylineWayPoints(
              origin = originEncoded,
              destination = destinationEncoded,
              apiKey = key,
              waypoints = waypoints)
          .observeForever { response -> _polylinePointsLiveData.value = response ?: listOf() }
    }
  }

  /** trivial getter */
  fun getPolylinePointsLiveData(): LiveData<List<LatLng>> {
    return polylinePointsLiveData
  }

  private val _locationsLiveData = MutableLiveData<List<Location>>()
  private val locationsLiveData: LiveData<List<Location>> = _locationsLiveData // gettable from view

  /* gets the places corresponding to the queried name */
  fun fetchPlaces(name: String) {
    val key = BuildConfig.GEOCODE_API_KEY
    viewModelScope.launch {
      locationsRepository.getPlaces(name = name, apiKey = key).observeForever { response ->
        _locationsLiveData.value = response ?: listOf()
      }
    }
  }

  fun getPlacesLiveData(): LiveData<List<Location>> {
    return locationsLiveData
  }

  /** @brief get a Flow of the user location updated every second */
  fun getUserLocation(): Flow<Location> {
    return locationClient.getLocationUpdates(1000)
  }

  fun getItineraryGoogleMapsURI(itinerary: Itinerary): String {
    val locations = itinerary.locations.map { "${it.lat},${it.long}" }
    val waypoints = locations.dropLast(1).joinToString("|")
    val uriString = "google.navigation:q=${locations.last()}&waypoints=${waypoints}&mode=w"
    return uriString
  }

  fun followItineraryOnGoogleMaps(context: Context, itinerary: Itinerary) {
    val uri = Uri.parse(getItineraryGoogleMapsURI(itinerary))
    val intent = Intent(Intent.ACTION_VIEW, uri)
    intent.setPackage("com.google.android.apps.maps")
    intent.resolveActivity(context.packageManager)?.let { context.startActivity(intent) }
  }

  /** Factory for creating a `ItineraryViewModel`. */
  class Factory(
      private val itineraryRepository: ItineraryRepository,
      private val directionsRepository: DirectionsRepository,
      private val locationsRepository: LocationsRepository,
      private val locationClient: LocationClient,
  ) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
      if (modelClass.isAssignableFrom(ItineraryViewModel::class.java)) {
        @Suppress("UNCHECKED_CAST")
        return ItineraryViewModel(
            itineraryRepository, directionsRepository, locationsRepository, locationClient)
            as T
      }
      throw IllegalArgumentException("Unknown ViewModel class")
    }
  }
}
