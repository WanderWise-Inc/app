package com.github.wanderwise_inc.app.data

import android.content.Context
import android.util.Log
import com.github.wanderwise_inc.app.isNetworkAvailable
import com.github.wanderwise_inc.app.model.profile.Profile
import com.github.wanderwise_inc.app.model.profile.ProfileLabels
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.suspendCancellableCoroutine

const val PROFILE_DB_PATH = "profiles"

/** @brief Implementation of the ProfileRepository interface */
class ProfileRepositoryImpl(db: FirebaseFirestore, val context: Context) : ProfileRepository {
  private val usersCollection = db.collection(PROFILE_DB_PATH)

  /**
   * @param userUid the uid of a user
   * @return a flow of a queried profile
   * @brief Queries a profile from the database
   */
  override fun getProfile(userUid: String): Flow<Profile?> {
    return flow {
          val document =
              suspendCancellableCoroutine<DocumentSnapshot> { continuation ->
                usersCollection
                    .document(userUid)
                    .get()
                    .addOnSuccessListener { documentSnapshot ->
                      Log.d("ProfileRepositoryImpl", "Successfully got profile")
                      continuation.resume(documentSnapshot)
                    }
                    .addOnFailureListener { exception ->
                      Log.w("ProfileRepositoryImpl", exception)
                      continuation.resumeWithException(exception)
                    }
              }
          if (document.exists()) {
            val profile = document.toObject(Profile::class.java)
            emit(profile)
          } else {
            emit(null)
          }
        }
        .catch { error ->
          Log.w("ProfileRepositoryImpl", error)
          emit(null)
        }
  }

  /**
   * @return a flow of all profiles
   * @brief Queries all profiles from the database
   */
  override fun getAllProfiles(): Flow<List<Profile>> {
    return flow {
          val allProfiles = suspendCancellableCoroutine { continuation ->
            usersCollection
                .get()
                .addOnSuccessListener { querySnapshot ->
                  Log.d("ProfileRepositoryImpl", "Successfully got all profiles")
                  val documents = querySnapshot.documents
                  val profiles = documents.mapNotNull { it.toObject(Profile::class.java) }
                  continuation.resume(profiles)
                }
                .addOnFailureListener { exception ->
                  Log.w("ProfileRepositoryImpl", exception)
                  continuation.resumeWithException(exception)
                }
          }
          emit(allProfiles)
        }
        .catch { error ->
          Log.w("ProfileRepositoryImpl", error)
          emit(listOf())
        }
  }

  /**
   * @param profile the profile to set
   * @brief Sets a user profile to Firestore
   */
  override fun setProfile(profile: Profile) {
    val userMap = profile.toMap()
    usersCollection
        .document(profile.userUid)
        .set(userMap)
        .addOnSuccessListener { Log.d("ProfileRepositoryImpl", "Successfully set profile") }
        .addOnFailureListener { exception ->
          Log.d("ProfileRepositoryImpl", "Failed to set profile")
          throw exception
        }
  }

  /**
   * @param profile the profile to delete
   * @brief Deletes a user profile from Firestore
   */
  override fun deleteProfile(profile: Profile) {
    usersCollection
        .document(profile.userUid)
        .delete()
        .addOnSuccessListener { Log.d("ProfileRepositoryImpl", "Successfully deleted profile") }
        .addOnFailureListener { exception ->
          Log.d("ProfileRepositoryImpl", "Failed to delete profile")
          throw exception
        }
  }

  /**
   * @param userUid the uid of a user
   * @param itineraryUid the uid of an itinerary
   * @brief Adds an itinerary to the user's liked itineraries
   */
  override fun addItineraryToLiked(userUid: String, itineraryUid: String) {
    usersCollection
        .document(userUid)
        .update(ProfileLabels.LIKED_ITINERARIES, FieldValue.arrayUnion(itineraryUid))
        .addOnSuccessListener {
          Log.d("ProfileRepositoryImpl", "Successfully added itinerary to liked")
        }
        .addOnFailureListener { exception ->
          Log.d("ProfileRepositoryImpl", "Failed to add itinerary to liked")
          throw exception
        }
  }

  /**
   * @param userUid the uid of a user
   * @param itineraryUid the uid of an itinerary
   * @brief Removes an itinerary from the user's liked itineraries
   */
  override fun removeItineraryFromLiked(userUid: String, itineraryUid: String) {
    usersCollection
        .document(userUid)
        .update(ProfileLabels.LIKED_ITINERARIES, FieldValue.arrayRemove(itineraryUid))
        .addOnSuccessListener {
          Log.d("ProfileRepositoryImplREMOVE", "Successfully removed itinerary from liked")
        }
        .addOnFailureListener { exception ->
          Log.d("ProfileRepositoryImplREMOVE", "Failed to remove itinerary from liked")
          throw exception
        }
  }

  /**
   * @param userUid the uid of a user
   * @param itineraryUid the uid of an itinerary
   * @return true if the itinerary is liked, false otherwise
   * @brief Checks if the given itinerary is contained in the user's liked itineraries
   */
  override suspend fun checkIfItineraryIsLiked(userUid: String, itineraryUid: String): Boolean {
    return when (context.isNetworkAvailable()) {
      true -> checkIfItineraryIsLikedOnline(userUid, itineraryUid)
      false -> false // sensible default. Applies only to locally saved itineraries
    }
  }

  /**
   * @param userUid the uid of a user
   * @param itineraryUid the uid of an itinerary
   * @return true if the itinerary is liked, false otherwise
   * @brief Checks if the given itinerary is contained in the user's liked itineraries (online mode)
   */
  private suspend fun checkIfItineraryIsLikedOnline(
      userUid: String,
      itineraryUid: String
  ): Boolean {
    val isLiked = suspendCancellableCoroutine { continuation ->
      usersCollection
          .document(userUid)
          .get()
          .addOnSuccessListener { documentSnapshot ->
            val likedItineraries =
                documentSnapshot.get(ProfileLabels.LIKED_ITINERARIES) as List<String>?
            Log.d("ProfileRepositoryImpl", "Liked itineraries: $likedItineraries")
            val isLiked = likedItineraries?.contains(itineraryUid) ?: false
            Log.d("ProfileRepositoryImpl", "Is liked: $isLiked")
            continuation.resume(isLiked)
          }
          .addOnFailureListener { exception -> continuation.resumeWithException(exception) }
    }
    return isLiked
  }

  /**
   * @param userUid the uid of a user
   * @brief Returns a list of user's liked itineraries in form of a Flow of List<String>
   */
  override fun getLikedItineraries(userUid: String): Flow<List<String>> {
    return flow {
          val likedItineraries = suspendCancellableCoroutine { continuation ->
            usersCollection
                .document(userUid)
                .get()
                .addOnSuccessListener { documentSnapshot ->
                  val likedItineraries =
                      documentSnapshot.get(ProfileLabels.LIKED_ITINERARIES) as List<String>?
                  Log.d("ProfileRepositoryImpl", "Liked itineraries: $likedItineraries")
                  continuation.resume(likedItineraries ?: listOf())
                }
                .addOnFailureListener { exception ->
                  Log.w("ProfileRepositoryImpl", exception)
                  continuation.resumeWithException(exception)
                }
          }
          emit(likedItineraries)
        }
        .catch { error ->
          Log.w("ProfileRepositoryImpl", error)
          emit(listOf())
        }
  }
}
