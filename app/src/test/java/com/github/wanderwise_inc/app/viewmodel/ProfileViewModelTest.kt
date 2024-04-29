package com.github.wanderwise_inc.app.viewmodel

import com.github.wanderwise_inc.app.DEFAULT_USER_UID
import com.github.wanderwise_inc.app.data.ImageRepository
import com.github.wanderwise_inc.app.data.ProfileRepository
import com.github.wanderwise_inc.app.model.location.FakeItinerary
import com.github.wanderwise_inc.app.model.profile.Profile
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.anyString
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnit
import org.mockito.junit.MockitoRule

class ProfileViewModelTest {

    @get:Rule
    val mockitoRule: MockitoRule = MockitoJUnit.rule()

    @Mock
    private lateinit var profileRepository: ProfileRepository

    @Mock
    private lateinit var imageRepository: ImageRepository

    private lateinit var profileViewModel: ProfileViewModel

    private val testProfile = Profile(
        uid = DEFAULT_USER_UID,
        displayName = "276746",
        userUid = "oscarduong",
        bio = "testing",
        profilePicture = null,
        likedItinerariesUid = mutableListOf(FakeItinerary.TOKYO.uid)
    )

    @Before
    fun setup() {
        profileViewModel = ProfileViewModel(profileRepository, imageRepository)
    }

    @Test
    fun getProfile() = runBlocking {
        `when`(profileRepository.getProfile(anyString())).thenReturn(flow { emit(testProfile) })

        val emittedProfile = profileViewModel.getProfile("oscarduong").first()
        assertEquals(testProfile, emittedProfile)
    }

    @Test
    fun getAllProfiles() = runBlocking {
        `when`(profileRepository.getAllProfiles()).thenReturn(flow { emit(listOf(testProfile)) })

        val emittedProfileList = profileViewModel.getAllProfiles().first()
        assertEquals(listOf(testProfile), emittedProfileList)
    }

    @Test
    fun setProfile() = runBlocking {
        val repo = mutableListOf<Profile>()

        `when`(profileRepository.setProfile(testProfile)).thenAnswer {
            val storedProfile = it.arguments[0] as Profile
            repo.add(storedProfile)
        }

        profileViewModel.setProfile(testProfile)
        assertEquals(testProfile, repo[0])
    }

    @Test
    fun deleteProfile() {
        val repo = mutableListOf(testProfile)

        `when`(profileRepository.deleteProfile(testProfile)).thenAnswer {
            val storedProfile = it.arguments[0] as Profile
            repo.remove(storedProfile)
        }

        profileViewModel.deleteProfile(testProfile)
        assertEquals(0, repo.size)
    }

    @Test
    fun getProfilePicture() = runBlocking {
        `when`(imageRepository.fetchImage(anyString())).thenReturn(flow { emit(null) })

        val emittedProfilePicture = profileViewModel.getProfilePicture(testProfile).first()
        assertNull(emittedProfilePicture)
    }

    @Test
    fun getDefaultProfilePicture() = runBlocking {
        `when`(imageRepository.fetchImage(anyString())).thenReturn(flow { emit(null) })

        val emittedProfilePicture = profileViewModel.getDefaultProfilePicture().first()
        assertNull(emittedProfilePicture)
    }

    @Test
    fun addLikedItinerary() {
        val repo = mutableMapOf<String, List<String>>()

        `when`(profileRepository.addItineraryToLiked(anyString(), anyString())).thenAnswer {
            val user = it.arguments[0] as String
            val itinerary = it.arguments[1] as String
            repo.put(user, listOf(itinerary))
        }

        profileViewModel.addLikedItinerary(testProfile.uid, FakeItinerary.TOKYO.uid)
        assertEquals(listOf(FakeItinerary.TOKYO.uid), repo[testProfile.uid])
    }

    @Test
    fun removeLikedItinerary() {
        val repo = mutableMapOf(testProfile.uid to mutableListOf(FakeItinerary.TOKYO.uid))

        `when`(profileRepository.removeItineraryFromLiked(anyString(), anyString())).thenAnswer {
            val user = it.arguments[0] as String
            val itinerary = it.arguments[1] as String
            repo[user]!!.remove(itinerary)
        }

        profileViewModel.removeLikedItinerary(testProfile.uid, FakeItinerary.TOKYO.uid)
        assertEquals(0, repo[testProfile.uid]!!.size)
    }

    @Test
    fun checkIfItineraryIsLiked() {
        val repo = mutableMapOf(testProfile.uid to mutableListOf(FakeItinerary.TOKYO.uid))

        `when`(profileRepository.checkIfItineraryIsLiked(anyString(), anyString())).thenAnswer {
            val user = it.arguments[0] as String
            val itinerary = it.arguments[1] as String
            repo[user]!!.contains(itinerary)
        }

        val isLiked = profileViewModel.checkIfItineraryIsLiked(testProfile.uid, FakeItinerary.TOKYO.uid)
        assertTrue(isLiked)
    }

    @Test
    fun getLikedItineraries() = runBlocking {
        `when`(profileRepository.getLikedItineraries(anyString())).thenReturn(
            flow { emit(listOf(FakeItinerary.TOKYO.uid)) }
        )

        val emittedLikedItineraryList = profileViewModel.getLikedItineraries(testProfile.uid).first()
        assertEquals(listOf(FakeItinerary.TOKYO.uid), emittedLikedItineraryList)
    }
}