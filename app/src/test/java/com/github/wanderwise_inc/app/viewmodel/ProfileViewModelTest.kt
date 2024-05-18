package com.github.wanderwise_inc.app.viewmodel

import com.github.wanderwise_inc.app.data.ImageRepository
import com.github.wanderwise_inc.app.data.ProfileRepository
import com.github.wanderwise_inc.app.model.location.FakeItinerary
import com.github.wanderwise_inc.app.model.profile.Profile
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.every
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class ProfileViewModelTest {

  @MockK private lateinit var profileRepository: ProfileRepository

  @MockK private lateinit var imageRepository: ImageRepository

  private lateinit var profileViewModel: ProfileViewModel

  private val testProfile =
      Profile(
          displayName = "276746",
          bio = "testing",
          userUid = "oscarduong",
          likedItinerariesUid = mutableListOf(FakeItinerary.TOKYO.uid))

  @Before
  fun setup() {
    MockKAnnotations.init(this)
    profileViewModel.setActiveProfile(testProfile)
    profileViewModel = ProfileViewModel(profileRepository, imageRepository)
  }

  @Test
  fun getProfile() = runBlocking {
    every { profileRepository.getProfile(any()) } returns flow { emit(testProfile) }

    val emittedProfile = profileViewModel.getProfile("oscarduong").first()
    assertEquals(testProfile, emittedProfile)
  }

  @Test
  fun getAllProfiles() = runBlocking {
    every { profileRepository.getAllProfiles() } returns flow { emit(listOf(testProfile)) }

    val emittedProfileList = profileViewModel.getAllProfiles().first()
    assertEquals(listOf(testProfile), emittedProfileList)
  }

  @Test
  fun setProfile() = runBlocking {
    val repo = mutableListOf<Profile>()

    coEvery { profileRepository.setProfile(any()) } answers
        {
          val profile = invocation.args[0] as Profile
          repo.add(profile)
        }

    profileViewModel.setProfile(testProfile)
    assertEquals(testProfile, repo[0])
  }

  @Test
  fun deleteProfile() {
    val repo = mutableListOf(testProfile)

    coEvery { profileRepository.deleteProfile(any()) } answers
        {
          val profile = invocation.args[0] as Profile
          repo.remove(profile)
        }

    profileViewModel.deleteProfile(testProfile)
    assertEquals(0, repo.size)
  }

  @Test
  fun getProfilePicture() = runBlocking {
    every { imageRepository.fetchImage(any()) } returns flow { emit(null) }

    val emittedProfilePicture = profileViewModel.getProfilePicture(testProfile).first()
    assertNull(emittedProfilePicture)
  }

  @Test
  fun getDefaultProfilePicture() = runBlocking {
    every { imageRepository.fetchImage(any()) } returns flow { emit(null) }

    val emittedProfilePicture = profileViewModel.getDefaultProfilePicture().first()
    assertNull(emittedProfilePicture)
  }

  @Test
  fun addLikedItinerary() {
    val repo = mutableMapOf<String, List<String>>()

    every { profileRepository.addItineraryToLiked(any(), any()) } answers
        {
          val user = invocation.args[0] as String
          val itinerary = invocation.args[1] as String
          repo[user] = listOf(itinerary)
        }

    profileViewModel.addLikedItinerary(testProfile.userUid, FakeItinerary.TOKYO.uid)
    assertEquals(listOf(FakeItinerary.TOKYO.uid), repo[testProfile.userUid])
  }

  @Test
  fun removeLikedItinerary() {
    val repo = mutableMapOf(testProfile.userUid to mutableListOf(FakeItinerary.TOKYO.uid))

    every { profileRepository.removeItineraryFromLiked(any(), any()) } answers
        {
          val user = invocation.args[0] as String
          val itinerary = invocation.args[1] as String
          repo[user]!!.remove(itinerary)
        }

    profileViewModel.removeLikedItinerary(testProfile.userUid, FakeItinerary.TOKYO.uid)
    assertEquals(0, repo[testProfile.userUid]!!.size)
  }

  @Test
  fun checkIfItineraryIsLiked() = runTest {
    val repo = mutableMapOf(testProfile.userUid to mutableListOf(FakeItinerary.TOKYO.uid))

    coEvery { profileRepository.checkIfItineraryIsLiked(any(), any()) } answers
        {
          val user = invocation.args[0] as String
          val itinerary = invocation.args[1] as String
          repo[user]!!.contains(itinerary)
        }

    val isLiked =
        profileViewModel.checkIfItineraryIsLiked(testProfile.userUid, FakeItinerary.TOKYO.uid)
    assertTrue(isLiked)
  }

  @Test
  fun getLikedItineraries() = runBlocking {
    every { profileRepository.getLikedItineraries(any()) } returns
        flow { emit(listOf(FakeItinerary.TOKYO.uid)) }

    val emittedLikedItineraryList =
        profileViewModel.getLikedItineraries(testProfile.userUid).first()
    assertEquals(listOf(FakeItinerary.TOKYO.uid), emittedLikedItineraryList)
  }
}
