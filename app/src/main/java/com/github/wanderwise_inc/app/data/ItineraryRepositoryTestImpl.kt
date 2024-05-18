package com.github.wanderwise_inc.app.data

import android.util.Log
import com.github.wanderwise_inc.app.model.location.Itinerary
import com.github.wanderwise_inc.app.model.location.Tag
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

/** @brief repository used for testing viewmodel functionality */
class ItineraryRepositoryTestImpl : ItineraryRepository {
  private val itineraries = mutableListOf<Itinerary>()
  private var uidCtr = 0

  override fun getPublicItineraries(): Flow<List<Itinerary>> {
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

  override fun addUserToLiked(userUid: String, itineraryUid: String) {
    return
  }

    override fun removeUserFromLiked(userUid: String, itineraryUid: String) {
        return
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

  override suspend fun writeItinerariesToDisk(itineraries: List<Itinerary>) {
    /* this test implementation has no disk interaction, so has the same effect as setting */
    for (itinerary in itineraries) setItinerary(itinerary)
  }
}
