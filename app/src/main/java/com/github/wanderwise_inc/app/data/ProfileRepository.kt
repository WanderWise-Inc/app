package com.github.wanderwise_inc.app.data

import com.github.wanderwise_inc.app.model.profile.Profile
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

interface ProfileRepository {
    /**
     * @param userUid the uid of a user
     * @return a flow of a queried profile
     */
    fun getProfile(userUid: String): Flow<Profile?>

    /**
     * @return a flow of all profiles
     */
    fun getAllProfiles(): Flow<List<Profile>>

    /**
     * @brief sets a user profile
     */
    fun setProfile(profile: Profile)

    /**
     * @brief deletes a user profile. Use sparingly
     */
    fun deleteProfile(profile: Profile)
}

/**
 * @brief test implementation of repository simulating a remote data source
 */
class ProfileRepositoryTestImpl : ProfileRepository {
    private var profiles = mutableListOf<Profile>()
    private var uidCtr = 0 // for getting a new uid

    /**
     * @throws NoSuchElementException if there are no matches
     */
    override fun getProfile(userUid: String): Flow<Profile?> {
        return flow {
            val filteredProfiles = profiles.filter { it.userUid == userUid }
            if (filteredProfiles.isEmpty())
                emit(null)
            else
                emit( filteredProfiles.first() )
        }
    }

    override fun getAllProfiles(): Flow<List<Profile>> {
        return flow { emit(profiles) }
    }

    override fun setProfile(profile: Profile) {
        profiles.remove(profile) // remove profile if it already contained
        if (profile.uid.isBlank()) {
            profile.uid = uidCtr.toString()
            uidCtr++
        }
        profiles.add(profile)
    }

    override fun deleteProfile(profile: Profile) {
        profiles.remove(profile)
    }
}
