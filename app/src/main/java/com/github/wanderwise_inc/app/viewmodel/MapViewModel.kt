package com.github.wanderwise_inc.app.viewmodel

import androidx.lifecycle.ViewModel
import com.github.wanderwise_inc.app.data.ItineraryRepository
import com.github.wanderwise_inc.app.model.location.Itinerary
import com.github.wanderwise_inc.app.model.location.ItineraryPreferences
import com.github.wanderwise_inc.app.model.location.Location
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.Polyline
import com.google.android.gms.maps.model.PolylineOptions
import kotlinx.coroutines.flow.Flow

/** @brief ViewModel class for providing `Location`s and `Itinerary`s to the map UI */
class MapViewModel(private val itineraryRepository: ItineraryRepository) : ViewModel() {
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

  public fun itineraryToPolyline(itinerary: Itinerary, googleMap: GoogleMap): Polyline {
    val polyline =
        googleMap.addPolyline(
            PolylineOptions().clickable(true).addAll(itinerary.locations.map { it.toLatLng() }))

    polyline.tag = itinerary

    return polyline
  }

  private fun createMarkerOption(location: Location): MarkerOptions {
    return MarkerOptions().position(location.toLatLng())
  }

  private fun locationToMarker(location: Location, googleMap: GoogleMap): Marker? {
    return googleMap.addMarker(createMarkerOption(location))
  }
}
