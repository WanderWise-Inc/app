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

class ProfileRepositoryImpl(db: FirebaseFirestore, val context: Context) : ProfileRepository {
  private val usersCollection = db.collection(PROFILE_DB_PATH)

  override fun getProfile(userUid: String): Flow<Profile?> {
    return flow {
          val document =
              suspendCancellableCoroutine<DocumentSnapshot> { continuation ->
                usersCollection
                    .document(userUid)
                    .get()
                    .addOnSuccessListener { documentSnapshot ->
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

  override fun removeItineraryFromLiked(userUid: String, itineraryUid: String) {
    Log.d("ProfileRepositoryImplREMOVE", "Removing itinerary from liked")
    Log.d("ProfileRepositoryImplREMOVE", "User: $userUid")
    Log.d("ProfileRepositoryImplREMOVE", "Itinerary: $itineraryUid")
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

  override suspend fun checkIfItineraryIsLiked(userUid: String, itineraryUid: String): Boolean {
    return when (context.isNetworkAvailable()) {
      true -> checkIfItineraryIsLikedOnline(userUid, itineraryUid)
      false -> false // sensible default. Applies only to locally saved itineraries
    }
  }

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
