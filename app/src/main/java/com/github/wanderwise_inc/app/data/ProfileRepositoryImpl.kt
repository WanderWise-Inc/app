package com.github.wanderwise_inc.app.data

import android.util.Log
import com.github.wanderwise_inc.app.model.location.ItineraryLabels
import com.github.wanderwise_inc.app.model.profile.Profile
import com.github.wanderwise_inc.app.model.profile.ProfileLabels
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class ProfileRepositoryImpl(db: FirebaseFirestore) : ProfileRepository {
  private val usersCollection = db.collection("users")

  override fun getProfile(userUid: String): Flow<Profile?> {
    return flow {
      val document = suspendCancellableCoroutine<DocumentSnapshot> {continuation ->
        usersCollection
          .document(userUid)
          .get()
          .addOnSuccessListener { documentSnapshot ->
            continuation.resume(documentSnapshot)
          }
          .addOnFailureListener { exception ->
            continuation.resumeWithException(exception)
          }
      }
      if (document.exists()) {
        val profile = document.toObject(Profile::class.java)
        emit(profile)
      } else {
        emit(null)
      }
    }.catch { emit(null) }
  }

  override fun getAllProfiles(): Flow<List<Profile>> {
    return flow {
      val allProfiles = suspendCancellableCoroutine { continuation ->
        usersCollection
          .get()
          .addOnSuccessListener { querySnapshot ->
            val documents = querySnapshot.documents
            val profiles = documents.mapNotNull { it.toObject(Profile::class.java) }
            continuation.resume(profiles)
          }
          .addOnFailureListener { exception ->
            continuation.resumeWithException(exception)
          }
      }
      emit(allProfiles)
    }.catch { emit(listOf()) }
  }

  override fun setProfile(profile: Profile) {
    val userMap = profile.toMap()
    usersCollection
        .document(profile.userUid)
        .set(userMap)
        .addOnSuccessListener { Log.d("ProfileRepositoryImpl", "Successfully set profile") }
        .addOnFailureListener {
          Log.d("ProfileRepositoryImpl", "Failed to set profile")
          throw it
        }
  }

  override fun deleteProfile(profile: Profile) {
    usersCollection
      .document(profile.userUid)
      .delete()
      .addOnSuccessListener {
        Log.d("ProfileRepositoryImpl", "Successfully deleted profile")
      }
      .addOnFailureListener {
        Log.d("ProfileRepositoryImpl", "Failed to delete profile")
        throw it
      }
  }

  override fun addItineraryToLiked(userUid: String, itineraryUid: String) {
    usersCollection
      .document(userUid)
      .update(ProfileLabels.LIKED_ITINERARIES, FieldValue.arrayUnion(itineraryUid))
      .addOnSuccessListener {
        Log.d("ProfileRepositoryImpl", "Successfully added itinerary to liked")
      }
      .addOnFailureListener {
        Log.d("ProfileRepositoryImpl", "Failed to add itinerary to liked")
        throw it
      }
  }

  override fun removeItineraryFromLiked(userUid: String, itineraryUid: String) {
    usersCollection
      .document(userUid)
      .update(ProfileLabels.LIKED_ITINERARIES, FieldValue.arrayRemove(itineraryUid))
      .addOnSuccessListener {
        Log.d("ProfileRepositoryImpl", "Successfully removed itinerary from liked")
      }
      .addOnFailureListener {
        Log.d("ProfileRepositoryImpl", "Failed to remove itinerary from liked")
        throw it
      }
  }

  override fun checkIfItineraryIsLiked(userUid: String, itineraryUid: String): Boolean {
    return true
  }

  override fun getLikedItineraries(userUid: String): Flow<List<String>> {
    return flow {
      val likedItineraries = suspendCancellableCoroutine { continuation ->
        usersCollection
          .document(userUid)
          .get()
          .addOnSuccessListener { documentSnapshot ->
            val likedItineraries = documentSnapshot.get(ProfileLabels.LIKED_ITINERARIES) as List<String>?
            continuation.resume(likedItineraries ?: listOf())
          }
          .addOnFailureListener { exception ->
            continuation.resumeWithException(exception)
          }
      }
      emit(likedItineraries)
    }.catch { emit(listOf()) }
  }
}
