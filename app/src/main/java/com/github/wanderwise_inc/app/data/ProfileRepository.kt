package com.github.wanderwise_inc.app.data

import com.github.wanderwise_inc.app.model.profile.Profile
import kotlinx.coroutines.flow.Flow

interface ProfileRepository {
  /**
   * @param userUid the uid of a user
   * @return a flow of a queried profile
   */
  fun getProfile(userUid: String): Flow<Profile?>

  /** @return a flow of all profiles */
  fun getAllProfiles(): Flow<List<Profile>>

  /** @brief sets a user profile */
  fun setProfile(profile: Profile)

  /** @brief deletes a user profile. Use sparingly */
  fun deleteProfile(profile: Profile)

  /** @brief add an Itinerary to the user's liked itineraries */
  fun addItineraryToLiked(userUid: String, itineraryUid: String)

  /** @brief remove an Itinerary to the user's liked itineraries */
  fun removeItineraryFromLiked(userUid: String, itineraryUid: String)

  /** @brief checks if the given Itinerary is contained in the user's liked itineraries */
  fun checkIfItineraryIsLiked(userUid: String, itineraryUid: String): Boolean

  /** @brief returns list of user's liked itineraries */
  fun getLikedItineraries(userUid: String): Flow<List<String>>
}
