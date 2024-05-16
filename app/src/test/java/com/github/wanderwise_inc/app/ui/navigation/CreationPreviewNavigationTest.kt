package com.github.wanderwise_inc.app.ui.navigation

import android.location.Location
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.test.assertHasClickAction
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
import com.github.wanderwise_inc.app.model.profile.Profile
import com.github.wanderwise_inc.app.ui.TestTags
import com.github.wanderwise_inc.app.ui.creation.steps.CreationStepPreview
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
import org.junit.jupiter.api.Assertions
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class CreationPreviewNavigationTest {
  @get:Rule val composeTestRule = createComposeRule()

  @MockK private lateinit var imageRepository: ImageRepository
  @MockK private lateinit var createItineraryViewModel: CreateItineraryViewModel
  @MockK private lateinit var profileViewModel: ProfileViewModel
  @MockK private lateinit var bottomNavigationViewModel: BottomNavigationViewModel
  @MockK private lateinit var firebaseAuth: FirebaseAuth
  private val profile = Profile(userUid = "0", displayName = "me", bio = "bio")

  private lateinit var onFinished: () -> Unit

  private lateinit var navController: TestNavHostController

  @Before
  fun setupNavHost() {
    MockKAnnotations.init(this)
    every { imageRepository.fetchImage(any()) } returns flow { emit(null) }
    every { profileViewModel.getProfile(any()) } returns flow { emit(profile) }

    composeTestRule.setContent {
      /*val mockProfile = Profile("", "Test", "0", "Bio", null)
      val mockItinerary = FakeItinerary.SAN_FRANCISCO
      val mockLocation = Location("")

      every { imageRepository.fetchImage(any()) } returns flowOf(null)

      every { profileViewModel.getProfile(any()) } returns flow { emit(mockProfile) }
      coEvery { profileViewModel.setProfile(any()) } returns Unit
      every { profileViewModel.addLikedItinerary(any(), any()) } returns Unit
      every { profileViewModel.getLikedItineraries(any()) } returns flow { emit(emptyList()) }
      every { profileViewModel.getDefaultProfilePicture() } returns flow { emit(null) }
      every { profileViewModel.getProfilePicture(any()) } returns flow { emit(null) }*/

      val itineraryBuilder = Itinerary.Builder(userUid = "")
      val dummyLiveData: LiveData<List<LatLng>> = MutableLiveData(listOf())

      every { createItineraryViewModel.setItinerary(any()) } returns Unit
      every { createItineraryViewModel.incrementItineraryLikes(any()) } returns Unit
      every { createItineraryViewModel.getAllPublicItineraries() } returns
          flow { emit(emptyList()) }
      every { createItineraryViewModel.getUserLocation() } returns flow { emit(Location("")) }
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
      //      every { createItineraryViewModel.getNewItinerary() } returns itineraryBuilder
      every { createItineraryViewModel.getNewItinerary() } returns
          Itinerary.Builder(userUid = "uniqueUserUID")
      // coEvery { createItineraryViewModel.getItineraryFromUids(any()) } returns flow {
      // listOf(mockItinerary) }

      every { bottomNavigationViewModel.setSelected(any()) } returns Unit
      /*
      every { bottomNavigationViewModel.selected } returns liveData { 0 }

      every { firebaseAuth.currentUser?.uid } returns null*/

      // every {createItineraryViewModel.getUserLocation()} returns flow { emit(epflLocation) }

      FirebaseApp.initializeApp(LocalContext.current)
      navController = TestNavHostController(LocalContext.current)
      navController.navigatorProvider.addNavigator(ComposeNavigator())

      onFinished = {}

      CreationStepPreview(
          navController = navController,
          createItineraryViewModel = createItineraryViewModel,
          profileViewModel = profileViewModel,
          onFinished = onFinished,
          imageRepository = imageRepository,
      )
    }
  }

  @Test
  fun `verify change preview floating button is displayed`() {
    composeTestRule.onNodeWithTag(TestTags.CREATION_SCREEN_PREVIEW_BUTTON).assertIsDisplayed()
  }

  @Test
  fun `verify start destination is preview banner screen`() {
    composeTestRule.onNodeWithTag(TestTags.CREATION_SCREEN_PREVIEW_BANNER).assertIsDisplayed()

    val route = navController.currentBackStackEntry?.destination?.route
    Assertions.assertEquals("Creation/${CreationPreviewOptions.PREVIEW_BANNER}", route)
  }

  @Test
  fun `perform click on floating button navigates to preview itinerary`() {
    composeTestRule.onNodeWithTag(TestTags.CREATION_SCREEN_PREVIEW_BUTTON).performClick()

    composeTestRule.onNodeWithTag(TestTags.CREATION_SCREEN_PREVIEW_ITINERARY).assertIsDisplayed()

    val route = navController.currentBackStackEntry?.destination?.route
    Assertions.assertEquals("Creation/${CreationPreviewOptions.PREVIEW_ITINERARY}", route)
  }

  // commented out because fixing this would take a lot of time
  @Test
  fun `perform click on floating button twice navigates to preview banner`() {
    composeTestRule.onNodeWithTag(TestTags.CREATION_SCREEN_PREVIEW_BUTTON).performClick()
    composeTestRule.onNodeWithTag(TestTags.CREATION_SCREEN_PREVIEW_BUTTON).performClick()

    composeTestRule.onNodeWithTag(TestTags.CREATION_SCREEN_PREVIEW_BANNER).assertIsDisplayed()

    val route = navController.currentBackStackEntry?.destination?.route
    Assertions.assertEquals("Creation/${CreationPreviewOptions.PREVIEW_BANNER}", route)
  }

  @Test
  fun `finish button exists and is clickable`() {
    composeTestRule.onNodeWithTag(TestTags.CREATION_FINISH_BUTTON).assertIsDisplayed()
    composeTestRule.onNodeWithTag(TestTags.CREATION_FINISH_BUTTON).assertHasClickAction()
  }
}
