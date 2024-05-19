package com.github.wanderwise_inc.app.data

import com.github.wanderwise_inc.app.model.location.Itinerary
import com.github.wanderwise_inc.app.model.location.Tag
import kotlinx.coroutines.flow.Flow

class E2EItineraryRepository : ItineraryRepository {
    override fun getPublicItineraries(): Flow<List<Itinerary>> {
        TODO("Not yet implemented")
    }

    override fun getUserItineraries(userUid: String): Flow<List<Itinerary>> {
        TODO("Not yet implemented")
    }

    override fun getItinerariesWithTags(tags: List<Tag>): Flow<List<Itinerary>> {
        TODO("Not yet implemented")
    }

    override suspend fun getItinerary(uid: String): Itinerary? {
        TODO("Not yet implemented")
    }

    override fun setItinerary(itinerary: Itinerary) {
        TODO("Not yet implemented")
    }

    override fun updateItinerary(oldUid: String, new: Itinerary) {
        TODO("Not yet implemented")
    }

    override fun deleteItinerary(itinerary: Itinerary) {
        TODO("Not yet implemented")
    }

    override suspend fun writeItinerariesToDisk(itineraries: List<Itinerary>) {
        TODO("Not yet implemented")
    }
}