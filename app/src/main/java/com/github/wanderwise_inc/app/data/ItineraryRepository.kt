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

    /** @brief gets an itinerary by its UID
     * @param uid the UID of the itinerary
     * */
  suspend fun getItinerary(uid: String): Itinerary?

  /** @brief sets an itinerary. If the itinerary has a blank UID, one will be generated
   * @param itinerary the itinerary to set
   * */
  fun setItinerary(itinerary: Itinerary)

  /** @brief update and itinerary.
   * @param oldUid the UID of the itinerary to update
   * @param new the new itinerary to set
   * */
  fun updateItinerary(oldUid: String, new: Itinerary)

  /** @brief deletes an itinerary
   * @param itinerary the itinerary to delete
   * */
  fun deleteItinerary(itinerary: Itinerary)

  /** @brief writes itineraries to persistent storage
   * @param itineraries the itineraries to write to disk
   * */
  suspend fun writeItinerariesToDisk(itineraries: List<Itinerary>)

    /** @return a new random Id for the itinerary */
  fun getNewId(): String
}
