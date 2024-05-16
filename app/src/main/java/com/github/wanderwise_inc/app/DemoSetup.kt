package com.github.wanderwise_inc.app

import com.github.wanderwise_inc.app.model.location.Itinerary
import com.github.wanderwise_inc.app.model.location.ItineraryTags
import com.github.wanderwise_inc.app.model.location.PlacesReader
import com.github.wanderwise_inc.app.model.profile.DEFAULT_OFFLINE_PROFILE
import com.github.wanderwise_inc.app.model.profile.Profile
import com.github.wanderwise_inc.app.viewmodel.ItineraryViewModel
import com.github.wanderwise_inc.app.viewmodel.ProfileViewModel
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

const val DEFAULT_USER_UID = "TEST_UID"
const val PREVIEW_ITINERARY_DEMO_UID = "DEMO" // Can be called from somewhere else
private const val OTHER_USER_UID = "OTHER_UID"

private var DEMO_CALLED = false

/** @brief isolates demo-related setup */
fun demoSetup(
    itineraryViewModel: ItineraryViewModel,
    profileViewModel: ProfileViewModel,
    firebaseAuth: FirebaseAuth
) {
  // was being called multiple times for some reason. The condition prevents this
  if (!DEMO_CALLED) {
    addProfiles(itineraryViewModel, profileViewModel, firebaseAuth)
    addItineraries(itineraryViewModel, profileViewModel, firebaseAuth)
    DEMO_CALLED = true
  }
}

/** @brief adds some profiles to ProfileViewModel */
fun addProfiles(
    itineraryViewModel: ItineraryViewModel,
    profileViewModel: ProfileViewModel,
    firebaseAuth: FirebaseAuth
) {
  val currentUserUid = firebaseAuth.currentUser?.uid ?: DEFAULT_USER_UID

  val someOtherProfile =
      Profile(
          userUid = OTHER_USER_UID,
          displayName = "John Doe",
          bio = "Wandering about",
      )

  runBlocking {
    val profile = profileViewModel.getProfile(currentUserUid).first()
    if (profile != null) profileViewModel.setProfile(Profile(currentUserUid))

    profileViewModel.setProfile(someOtherProfile)
  }
}

/** @brief adds some itineraries to ItineraryViewModel */
fun addItineraries(
    itineraryViewModel: ItineraryViewModel,
    profileViewModel: ProfileViewModel,
    firebaseAuth: FirebaseAuth
) {
  val defaultLocations = PlacesReader(null).readFromString()

  profileViewModel.setActiveProfile(DEFAULT_OFFLINE_PROFILE)

  val itineraryAdventureAndLuxury =
      Itinerary(
          userUid = OTHER_USER_UID,
          locations = defaultLocations,
          title = "Shopping then adventure",
          tags = listOf(ItineraryTags.ADVENTURE, ItineraryTags.LUXURY),
          description = "gucci",
          visible = true,
      )

  val itineraryAdventure =
      Itinerary(
          userUid = profileViewModel.getActiveUserUid(),
          locations = defaultLocations,
          title = "Hike",
          tags =
              listOf(
                  ItineraryTags.ACTIVE,
                  ItineraryTags.PHOTOGRAPHY,
                  ItineraryTags.NATURE,
                  ItineraryTags.ADVENTURE,
                  ItineraryTags.FOODIE,
                  ItineraryTags.RURAL,
                  ItineraryTags.WILDLIFE,
                  ItineraryTags.WELLNESS),
          description = null,
          visible = true,
          time = 3,
          price = 25.0f,
      )

  /** default profile for demo-ing */
  val dummyProfile = Profile(userUid = "-1", bio = "uwu", displayName = "John Doe")

  val privateItinerary =
      Itinerary(
          userUid = profileViewModel.getActiveUserUid(),
          locations = defaultLocations,
          title = "My private itinerary",
          tags = listOf(ItineraryTags.ADVENTURE),
          description = null,
          visible = false,
      )

  val publicItinerary =
      Itinerary(
          uid = PREVIEW_ITINERARY_DEMO_UID,
          userUid = profileViewModel.getActiveUserUid(),
          locations = defaultLocations,
          title = "San Francisco Bike Itinerary",
          tags = listOf(ItineraryTags.CULTURAL, ItineraryTags.NATURE, ItineraryTags.BUDGET),
          description = "A 3-day itinerary to explore the best of San Francisco on a bike.",
          visible = true)

  itineraryViewModel.setItinerary(itineraryAdventure)
  itineraryViewModel.setItinerary(itineraryAdventureAndLuxury)
  itineraryViewModel.setItinerary(privateItinerary)
  itineraryViewModel.setItinerary(publicItinerary)

  for (i in 0..1023) itineraryViewModel.incrementItineraryLikes(publicItinerary)

  // other profile likes their own itinerary
  profileViewModel.addLikedItinerary(profileViewModel.getActiveUserUid(), itineraryAdventure.uid)
}
