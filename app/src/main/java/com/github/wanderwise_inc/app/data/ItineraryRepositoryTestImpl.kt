package com.github.wanderwise_inc.app.data

import android.util.Log
import com.github.wanderwise_inc.app.model.location.Itinerary
import com.github.wanderwise_inc.app.model.location.ItineraryLabels
import com.github.wanderwise_inc.app.model.location.Tag
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.suspendCancellableCoroutine

/** @brief repository used for testing viewmodel functionality */
class ItineraryRepositoryTestImpl : ItineraryRepository {
  private val itineraries = mutableListOf<Itinerary>()
  private var uidCtr = 0

  override fun getPublicItineraries(): Flow<List<Itinerary>> {
    print(itineraries)
    return flow { emit(itineraries.filter { it.visible }) }
  }

  override fun getUserItineraries(userUid: String): Flow<List<Itinerary>> {
    return flow { emit(itineraries.filter { it.userUid == userUid }) }
  }

  override fun getItinerariesWithTags(tags: List<Tag>): Flow<List<Itinerary>> {
    return flow {
      emit(itineraries.filter { it.tags.toSet().intersect(tags.toSet()).isNotEmpty() })
    }
  }

  override suspend fun getItinerary(uid: String): Itinerary {
    return itineraries.first { it.uid == uid }
  }

  override fun setItinerary(itinerary: Itinerary) {
    itineraries.remove(itinerary) // remove if the itinerary is already present
    if (itinerary.uid.isBlank()) {
      itinerary.uid = uidCtr.toString()
      uidCtr++
    }
    itineraries.add(itinerary)
  }

  override fun updateItinerary(oldUid: String, new: Itinerary) {
    assert(oldUid == new.uid)
    itineraries.removeIf { it.uid == oldUid }
    itineraries.add(new)
    Log.d("ITINERARY_UPDATE", "$new")
  }

  override fun deleteItinerary(itinerary: Itinerary) {
    itineraries.remove(itinerary)
  }
}

