package com.github.wanderwise_inc.app

import android.util.Log
import com.github.wanderwise_inc.app.model.location.Itinerary
import com.github.wanderwise_inc.app.model.location.ItineraryTags
import com.github.wanderwise_inc.app.model.location.PlacesReader
import com.github.wanderwise_inc.app.model.profile.Profile
import com.github.wanderwise_inc.app.viewmodel.MapViewModel
import com.github.wanderwise_inc.app.viewmodel.ProfileViewModel
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

const val DEFAULT_USER_UID = "TEST_UID"
private const val OTHER_USER_UID = "OTHER_UID"

private var DEMO_CALLED = false

/** @brief isolates demo-related setup */
fun demoSetup(mapViewModel: MapViewModel, profileViewModel: ProfileViewModel, firebaseAuth: FirebaseAuth) {
  // was being called multiple times for some reason. The condition prevents this
  Log.d("DEMO_SETUP", "demo setup started")
  if (!DEMO_CALLED) {
    addProfiles(mapViewModel, profileViewModel, firebaseAuth)
    addItineraries(mapViewModel, profileViewModel)
    DEMO_CALLED = true
  }
}

/** @brief adds some profiles to ProfileViewModel */
fun addProfiles(mapViewModel: MapViewModel, profileViewModel: ProfileViewModel, firebaseAuth: FirebaseAuth) {
  val currentUser = firebaseAuth.currentUser
  val currentUserUid = currentUser?.uid ?: DEFAULT_USER_UID

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

/** @brief adds some itineraries to MapViewModel */
fun addItineraries(mapViewModel: MapViewModel, profileViewModel: ProfileViewModel) {
  val defaultLocations = PlacesReader(null).readFromString()

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
          userUid = OTHER_USER_UID,
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
      )
  val currentUser = FirebaseAuth.getInstance().currentUser
  val currentUserUid = currentUser?.uid ?: DEFAULT_USER_UID

  val privateItinerary =
      Itinerary(
          userUid = currentUserUid,
          locations = defaultLocations,
          title = "My private itinerary",
          tags = listOf(ItineraryTags.ADVENTURE),
          description = null,
          visible = false,
      )

  val publicItinerary =
      Itinerary(
          userUid = currentUserUid,
          locations = defaultLocations,
          title = "My public itinerary",
          tags = listOf(ItineraryTags.LUXURY),
          description = null,
          visible = true,
      )

  mapViewModel.setItinerary(itineraryAdventure)
  mapViewModel.setItinerary(itineraryAdventureAndLuxury)
  mapViewModel.setItinerary(privateItinerary)
  mapViewModel.setItinerary(publicItinerary)

  // other profile likes their own itinerary
  profileViewModel.addLikedItinerary(OTHER_USER_UID, itineraryAdventure.uid)
}
