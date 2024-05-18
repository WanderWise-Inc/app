package com.github.wanderwise_inc.app.data

import com.github.wanderwise_inc.app.model.location.Itinerary
import com.github.wanderwise_inc.app.model.location.Tag
import kotlinx.coroutines.flow.Flow

interface ItineraryRepository {
  /** @return a list of all public itineraries */
  fun getPublicItineraries(): Flow<List<Itinerary>>

  /**
   * @param userUid acts as a foreign key to user table
   * @return all itineraries created by user with uid `userUid`
   */
  fun getUserItineraries(userUid: String): Flow<List<Itinerary>>

  /**
   * @param tags
   * @return all itineraries with at least one matching tag with `tags`
   */
  fun getItinerariesWithTags(tags: List<Tag>): Flow<List<Itinerary>>

  fun addUserToLiked(userUid: String, itineraryUid: String)

  fun removeUserFromLiked(userUid: String, itineraryUid: String)

  suspend fun getItinerary(uid: String): Itinerary

  /** @brief sets an itinerary. If the itinerary has a blank UID, one will be generated */
  fun setItinerary(itinerary: Itinerary)

  /** @brief update and itinerary. */
  fun updateItinerary(oldUid: String, new: Itinerary)

  /** @brief deletes an itinerary */
  fun deleteItinerary(itinerary: Itinerary)

  fun getLikedUsers(itineraryUid: String): Flow<List<String>>

  /** @brief writes itineraries to persistent storage */
  suspend fun writeItinerariesToDisk(itineraries: List<Itinerary>)
}
