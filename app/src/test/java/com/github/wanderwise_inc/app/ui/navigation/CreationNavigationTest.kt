package com.github.wanderwise_inc.app.ui.navigation

import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.navigation.compose.ComposeNavigator
import androidx.navigation.testing.TestNavHostController
import com.github.wanderwise_inc.app.data.ImageRepository
import com.github.wanderwise_inc.app.model.location.Itinerary
import com.github.wanderwise_inc.app.model.location.Location
import com.github.wanderwise_inc.app.model.profile.Profile
import com.github.wanderwise_inc.app.ui.TestTags
import com.github.wanderwise_inc.app.ui.creation.CreationScreen
import com.github.wanderwise_inc.app.viewmodel.BottomNavigationViewModel
import com.github.wanderwise_inc.app.viewmodel.CreateItineraryViewModel
import com.github.wanderwise_inc.app.viewmodel.ProfileViewModel
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.flow.flow
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.runner.RunWith
import org.mockito.kotlin.or
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class CreationNavigationTest {
  @get:Rule val composeTestRule = createComposeRule()

  @MockK private lateinit var imageRepository: ImageRepository
  @MockK private lateinit var createItineraryViewModel: CreateItineraryViewModel
  @MockK private lateinit var profileViewModel: ProfileViewModel
  @MockK private lateinit var bottomNavigationViewModel: BottomNavigationViewModel
  @MockK private lateinit var firebaseAuth: FirebaseAuth
  @MockK private lateinit var onFinished: () -> Unit
  private val profile = Profile(userUid = "0", displayName = "me", bio = "bio")

  private lateinit var navController: TestNavHostController

  @Before
  fun setupNavHost() {
    MockKAnnotations.init(this)
    every { imageRepository.fetchImage(any()) } returns flow { emit(null) }
    every { profileViewModel.getProfile(any()) } returns flow { emit(profile) }
    every { profileViewModel.getUserUid() } returns profile.userUid

    composeTestRule.setContent {
      val itineraryBuilder = Itinerary.Builder(userUid = "")
      val dummyLiveData: LiveData<List<LatLng>> = MutableLiveData(listOf())

      every { createItineraryViewModel.setItinerary(any()) } returns Unit
      every { createItineraryViewModel.incrementItineraryLikes(any()) } returns Unit
      every { createItineraryViewModel.getAllPublicItineraries() } returns
          flow { emit(emptyList()) }
      every { createItineraryViewModel.getUserLocation() } returns flow { emit(Location(0.0, 0.0)) }
      every { createItineraryViewModel.getUserItineraries(any()) } returns
          flow { emit(emptyList()) }
      every { createItineraryViewModel.getItineraryFromUids(any()) } returns
          flow { emit(emptyList()) }
      every { createItineraryViewModel.setFocusedItinerary(any()) } returns Unit
      every { createItineraryViewModel.getFocusedItinerary() } returns null
      every { createItineraryViewModel.setItinerary(any()) } returns Unit
      every { createItineraryViewModel.getNewItinerary() } returns itineraryBuilder
      every { createItineraryViewModel.fetchPolylineLocations(any()) } returns Unit
      every { createItineraryViewModel.getPolylinePointsLiveData() } returns dummyLiveData
      every { createItineraryViewModel.getNewItinerary() } returns
          Itinerary.Builder(userUid = "uniqueUserUID")

      every { firebaseAuth.currentUser?.uid } returns null

      FirebaseApp.initializeApp(LocalContext.current)
      navController = TestNavHostController(LocalContext.current)
      navController.navigatorProvider.addNavigator(ComposeNavigator())
      onFinished = {}

      CreationScreen(
          createItineraryViewModel, profileViewModel, onFinished, navController, imageRepository)
    }
  }

  @Test
  fun `verify top navigation bar is displayed`() {
    composeTestRule.onNodeWithTag(TestTags.CREATION_SCREEN_NAV_BAR).assertIsDisplayed()
  }

  @Test
  fun `verify start destination is overview screen`() {
    composeTestRule.onNodeWithTag(TestTags.MAP_GOOGLE_MAPS).assertIsDisplayed()

    val route = navController.currentBackStackEntry?.destination?.route
    assertEquals("Creation/${CreationStepsRoute.LOCATIONS}", route)
  }

  @Test
  fun `perform click on description navigates to description`() {
    composeTestRule.onNodeWithTag("Creation/${CreationStepsRoute.DESCRIPTION}").performClick()

    composeTestRule.onNodeWithTag(TestTags.CREATION_SCREEN_DESCRIPTION_TITLE).assertIsDisplayed()

    val route = navController.currentBackStackEntry?.destination?.route
    assertEquals("Creation/${CreationStepsRoute.DESCRIPTION}", route)
  }

  @Test
  fun `perform click on tags navigates to tags`() {
    composeTestRule.onNodeWithTag("Creation/${CreationStepsRoute.TAGS}").performClick()

    composeTestRule.onNodeWithTag(TestTags.CREATION_SCREEN_TAGS).assertIsDisplayed()

    val route = navController.currentBackStackEntry?.destination?.route
    assertEquals("Creation/${CreationStepsRoute.TAGS}", route)
  }

  @Test
  fun `perform click on preview navigates to preview`() {
    composeTestRule.onNodeWithTag("Creation/${CreationStepsRoute.PREVIEW}").performClick()

    composeTestRule.onNodeWithTag(TestTags.CREATION_SCREEN_PREVIEW).assertIsDisplayed()

    val route = navController.currentBackStackEntry?.destination?.route
    assertEquals("Creation/${CreationStepsRoute.PREVIEW}", route)
  }
}
