package com.github.wanderwise_inc.app.data

import com.github.wanderwise_inc.app.model.profile.Profile
import kotlinx.coroutines.flow.Flow

class ProfileRepositoryImpl : ProfileRepository {
    override fun getProfile(userUid: String): Flow<Profile?> {
        TODO("Not yet implemented")
    }

    override fun getAllProfiles(): Flow<List<Profile>> {
        TODO("Not yet implemented")
    }

    override suspend fun setProfile(profile: Profile) {
        TODO("Not yet implemented")
    }

    override fun deleteProfile(profile: Profile) {
        TODO("Not yet implemented")
    }

    override fun addItineraryToLiked(userUid: String, itineraryUid: String) {
        TODO("Not yet implemented")
    }

    override fun removeItineraryFromLiked(userUid: String, itineraryUid: String) {
        TODO("Not yet implemented")
    }

    override fun checkIfItineraryIsLiked(userUid: String, itineraryUid: String): Boolean {
        TODO("Not yet implemented")
    }

    override fun getLikedItineraries(userUid: String): Flow<List<String>> {
        TODO("Not yet implemented")
    }
}