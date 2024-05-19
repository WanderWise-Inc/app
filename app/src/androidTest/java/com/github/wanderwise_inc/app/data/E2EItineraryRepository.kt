package com.github.wanderwise_inc.app.data

import com.github.wanderwise_inc.app.model.location.FakeItinerary
import com.github.wanderwise_inc.app.model.location.Itinerary
import com.github.wanderwise_inc.app.model.location.Tag
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class E2EItineraryRepository : ItineraryRepository {
  private val _repository =
      mutableListOf(FakeItinerary.TOKYO, FakeItinerary.SAN_FRANCISCO, FakeItinerary.SWITZERLAND)

  private val repository: List<Itinerary>
    get() = _repository

  override fun getPublicItineraries(): Flow<List<Itinerary>> {
    return flow { emit(repository) }
  }

  override fun getUserItineraries(userUid: String): Flow<List<Itinerary>> {
    return flow{ emit(_repository.filter { iti -> iti.userUid == userUid }) }
  }

  override fun getItinerariesWithTags(tags: List<Tag>): Flow<List<Itinerary>> {
    TODO("Not yet implemented")
  }

  override suspend fun getItinerary(uid: String): Itinerary? {
    return repository.find { it.uid == uid }
  }

  override fun setItinerary(itinerary: Itinerary) {
    _repository.add(itinerary)
  }

  override fun updateItinerary(oldUid: String, new: Itinerary) {
    _repository.removeIf { it.uid == oldUid }
    _repository.add(new)
  }

  override fun deleteItinerary(itinerary: Itinerary) {
    TODO("Not yet implemented")
  }

  override suspend fun writeItinerariesToDisk(itineraries: List<Itinerary>) {
    TODO("Not yet implemented")
  }
}
