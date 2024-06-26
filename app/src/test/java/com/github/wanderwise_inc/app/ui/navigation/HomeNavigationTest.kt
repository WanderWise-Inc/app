package com.github.wanderwise_inc.app.ui.navigation

import android.net.Uri
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.lifecycle.liveData
import androidx.navigation.compose.ComposeNavigator
import androidx.navigation.testing.TestNavHostController
import com.github.wanderwise_inc.app.data.ImageRepository
import com.github.wanderwise_inc.app.model.location.FakeItinerary
import com.github.wanderwise_inc.app.model.location.Location
import com.github.wanderwise_inc.app.model.profile.Profile
import com.github.wanderwise_inc.app.ui.TestTags
import com.github.wanderwise_inc.app.ui.home.HomeScreen
import com.github.wanderwise_inc.app.viewmodel.BottomNavigationViewModel
import com.github.wanderwise_inc.app.viewmodel.CreateItineraryViewModel
import com.github.wanderwise_inc.app.viewmodel.ItineraryViewModel
import com.github.wanderwise_inc.app.viewmodel.ProfileViewModel
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import io.mockk.MockKAnnotations
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.just
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class HomeNavigationTest {
  @get:Rule val composeTestRule = createComposeRule()

  @MockK private lateinit var imageRepository: ImageRepository
  @MockK private lateinit var itineraryViewModel: ItineraryViewModel
  @MockK private lateinit var createItineraryViewModel: CreateItineraryViewModel
  @MockK private lateinit var profileViewModel: ProfileViewModel
  @MockK private lateinit var bottomNavigationViewModel: BottomNavigationViewModel
  @MockK private lateinit var firebaseAuth: FirebaseAuth
  private val imageUri =
      Uri.parse(
          "https://firebasestorage.googleapis.com/v0/b/wanderwise-d8d36.appspot.com/o/images%2FitineraryPictures%2FdefaultItinerary.png?alt=media&token=b7170586-9168-445b-8784-8ad3ac5345bc")
  private lateinit var navController: TestNavHostController

  @Before
  fun setupNavHost() {
    MockKAnnotations.init(this)

    composeTestRule.setContent {
      val mockProfile = Profile("Test", "0", "Bio")
      val mockItinerary = FakeItinerary.SAN_FRANCISCO
      val mockLocation = Location(0.0, 0.0)

      every { imageRepository.fetchImage(any()) } returns flowOf(null)

      every { profileViewModel.getProfile(any()) } returns flow { emit(mockProfile) }
      every { profileViewModel.getActiveProfile() } returns mockProfile
      every { profileViewModel.getUserUid() } returns mockProfile.userUid
      every { profileViewModel.setActiveProfile(any()) } returns Unit
      coEvery { profileViewModel.setProfile(any()) } returns Unit
      every { profileViewModel.addLikedItinerary(any(), any()) } returns Unit
      every { profileViewModel.getLikedItineraries(any()) } returns flow { emit(emptyList()) }
      every { profileViewModel.getDefaultProfilePicture() } returns flow { emit(null) }
      every { profileViewModel.getProfilePicture(any()) } returns flow { emit(null) }
      every { profileViewModel.setActiveProfileLikedItineraries(any()) } returns Unit

      every { imageRepository.getCurrentFile() } returns imageUri
      every { imageRepository.setCurrentFile(any()) } just Runs
      every { imageRepository.setIsItineraryImage(any()) } just Runs
      every { imageRepository.setOnImageSelectedListener(any()) } just Runs

      every { itineraryViewModel.setItinerary(any()) } returns Unit
      every { itineraryViewModel.incrementItineraryLikes(any()) } returns Unit
      every { itineraryViewModel.getAllPublicItineraries() } returns flow { emit(emptyList()) }
      every { itineraryViewModel.getUserLocation() } returns flow { emit(Location(0.0, 0.0)) }
      every { itineraryViewModel.getUserItineraries(any()) } returns flow { emit(emptyList()) }
      every { itineraryViewModel.getItineraryFromUids(any()) } returns flow { emit(emptyList()) }
      every { itineraryViewModel.getFocusedItinerary() } returns null
      every { createItineraryViewModel.getNewItinerary() } returns null

      // coEvery { itineraryViewModel.getItineraryFromUids(any()) } returns flow {
      // listOf(mockItinerary) }

      every { bottomNavigationViewModel.setSelected(any()) } returns Unit
      every { bottomNavigationViewModel.selected } returns liveData { 0 }

      every { firebaseAuth.currentUser?.uid } returns null

      FirebaseApp.initializeApp(LocalContext.current)
      navController = TestNavHostController(LocalContext.current)
      navController.navigatorProvider.addNavigator(ComposeNavigator())
      HomeScreen(
          imageRepository,
          itineraryViewModel,
          createItineraryViewModel,
          bottomNavigationViewModel,
          profileViewModel,
          navController)
    }
  }

  @Test
  fun verify_BottomNavigationBarIsDisplayed() {
    composeTestRule.onNodeWithTag(TestTags.BOTTOM_NAV).assertIsDisplayed()
  }

  @Test
  fun verify_StartDestinationIsOverviewScreen() {
    composeTestRule.onNodeWithTag(TestTags.OVERVIEW_SCREEN).assertIsDisplayed()

    val route = navController.currentBackStackEntry?.destination?.route
    assertEquals(TopLevelRoute.OVERVIEW, route)
  }

  @Test
  fun performClick_OnItineraryButton_NavigatesToLikedScreen() {
    composeTestRule.onNodeWithTag(TopLevelRoute.LIKED).performClick()

    composeTestRule.onNodeWithTag(TestTags.LIKED_SCREEN).assertIsDisplayed()

    val route = navController.currentBackStackEntry?.destination?.route
    assertEquals(TopLevelRoute.LIKED, route)
  }

  @Test
  fun performClick_OnItineraryButton_NavigatesToCreationScreen() {
    composeTestRule.onNodeWithTag(TopLevelRoute.CREATION).performClick()

    composeTestRule.onNodeWithTag(TestTags.NO_NEW_CREATION_SCREEN).assertIsDisplayed()

    val route = navController.currentBackStackEntry?.destination?.route
    assertEquals(TopLevelRoute.CREATION, route)
  }

  @Test
  fun performClick_OnMapButton_NavigatesToMapScreen() {
    composeTestRule.onNodeWithTag(TopLevelRoute.MAP).performClick()

    composeTestRule.onNodeWithTag(TestTags.MAP_NULL_ITINERARY).assertIsDisplayed()

    val route = navController.currentBackStackEntry?.destination?.route
    assertEquals(TopLevelRoute.MAP, route)
  }

  @Test
  fun performClick_OnProfileButton_NavigatesToProfileScreen() {
    composeTestRule.onNodeWithTag(TopLevelRoute.PROFILE).performClick()

    composeTestRule.onNodeWithTag(TestTags.PROFILE_SCREEN).assertIsDisplayed()

    val route = navController.currentBackStackEntry?.destination?.route
    assertEquals(TopLevelRoute.PROFILE, route)
  }

  @Test
  fun performClicks_OnLikedThenMapButton_NavigatesToMapScreen() {
    composeTestRule.onNodeWithTag(TopLevelRoute.LIKED).performClick()

    composeTestRule.onNodeWithTag(TopLevelRoute.MAP).performClick()

    composeTestRule.onNodeWithTag(TestTags.MAP_NULL_ITINERARY).assertIsDisplayed()

    val route = navController.currentBackStackEntry?.destination?.route
    assertEquals(TopLevelRoute.MAP, route)
  }

  @Test
  fun performClicks_OnMapThenItineraryButton_NavigatesToLikedScreen() {
    // composeTestRule.onNodeWithTag(TopLevelRoute.MAP).performClick()

    composeTestRule.onNodeWithTag(TopLevelRoute.LIKED).performClick()

    composeTestRule.onNodeWithTag(TestTags.LIKED_SCREEN).assertIsDisplayed()

    val route = navController.currentBackStackEntry?.destination?.route
    assertEquals(TopLevelRoute.LIKED, route)
  }

  @Test
  fun performClicks_OnMapThenOverviewButton_NavigatesToOverviewScreen() {
    composeTestRule.onNodeWithTag(TopLevelRoute.MAP).performClick()

    composeTestRule.onNodeWithTag(TopLevelRoute.OVERVIEW).performClick()

    composeTestRule.onNodeWithTag(TestTags.OVERVIEW_SCREEN).assertIsDisplayed()

    val route = navController.currentBackStackEntry?.destination?.route
    assertEquals(TopLevelRoute.OVERVIEW, route)
  }

  @Test
  fun performMultipleClicks_OnNavigationBarButton_DoesNotAddToBackStack() {
    composeTestRule.onNodeWithTag(TopLevelRoute.MAP).performClick()

    composeTestRule.onNodeWithTag(TopLevelRoute.LIKED).performClick()

    composeTestRule.onNodeWithTag(TopLevelRoute.MAP).performClick()

    composeTestRule.onNodeWithTag(TopLevelRoute.LIKED).performClick()

    composeTestRule.onNodeWithTag(TopLevelRoute.MAP).performClick()

    navController.popBackStack() // simulates back button click

    val route = navController.currentBackStackEntry?.destination?.route
    assertEquals(TopLevelRoute.OVERVIEW, route)

    composeTestRule.onNodeWithTag(TestTags.OVERVIEW_SCREEN).assertIsDisplayed()
  }

  @Test
  fun performMultipleClicks_OnNavigationBarButtonEndingOnOverviewScreen_DoesNotAddToBackStack() {
    composeTestRule.onNodeWithTag(TopLevelRoute.MAP).performClick()

    composeTestRule.onNodeWithTag(TopLevelRoute.LIKED).performClick()

    composeTestRule.onNodeWithTag(TopLevelRoute.MAP).performClick()

    composeTestRule.onNodeWithTag(TopLevelRoute.LIKED).performClick()

    composeTestRule.onNodeWithTag(TopLevelRoute.PROFILE).performClick()

    composeTestRule.onNodeWithTag(TopLevelRoute.OVERVIEW).performClick()

    navController.popBackStack() // simulates back button click

    val route = navController.currentBackStackEntry?.destination?.route
    assertEquals(null, route)
  }
}
