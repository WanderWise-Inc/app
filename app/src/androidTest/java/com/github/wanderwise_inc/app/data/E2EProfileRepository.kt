package com.github.wanderwise_inc.app.data

import com.github.wanderwise_inc.app.model.profile.Profile
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class E2EProfileRepository : ProfileRepository {
    private val repository = listOf(
        Profile(
            userUid = "e2e_test_user_uid",
            displayName = "E2E Test User",
            bio = "SIGMA E2E BIO",
            likedItinerariesUid = mutableListOf()
        )
    )
    override fun getProfile(userUid: String): Flow<Profile?> {
        val profile = repository.find { it.userUid == userUid }
        return flow { emit(profile) }
    }

    override fun getAllProfiles(): Flow<List<Profile>> {
        TODO("Not yet implemented")
    }

    override fun setProfile(profile: Profile) {
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

    override suspend fun checkIfItineraryIsLiked(userUid: String, itineraryUid: String): Boolean {
        TODO("Not yet implemented")
    }

    override fun getLikedItineraries(userUid: String): Flow<List<String>> {
        TODO("Not yet implemented")
    }
}