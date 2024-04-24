package com.github.wanderwise_inc.app.viewmodel

import android.location.Location
import androidx.lifecycle.ViewModel
import com.github.wanderwise_inc.app.data.ItineraryRepository
import com.github.wanderwise_inc.app.model.location.Itinerary
import com.github.wanderwise_inc.app.model.location.ItineraryPreferences
import kotlinx.coroutines.flow.Flow

/** @brief ViewModel class for providing `Location`s and `Itinerary`s to the map UI */
class MapViewModel(
  private val itineraryRepository: ItineraryRepository,
  private val userLocationClient: UserLocationClient
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

  fun getUserLocation(): Flow<Location> {
    return userLocationClient.getLocationUpdates(1000)
  }
}
