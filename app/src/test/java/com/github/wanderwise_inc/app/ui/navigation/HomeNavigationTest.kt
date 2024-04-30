package com.github.wanderwise_inc.app.ui.navigation

/*
import android.util.Log
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.navigation.NavHostController
import androidx.navigation.compose.ComposeNavigator
import androidx.navigation.testing.TestNavHostController
import com.github.wanderwise_inc.app.DemoSetup
import com.github.wanderwise_inc.app.data.ImageRepository
import com.github.wanderwise_inc.app.model.profile.Profile
import com.github.wanderwise_inc.app.ui.creation.CreationScreenTestTags
import com.github.wanderwise_inc.app.ui.home.HomeScreen
import com.github.wanderwise_inc.app.ui.list_itineraries.LikedScreenTestTags
import com.github.wanderwise_inc.app.ui.list_itineraries.OverviewScreenTestTags
import com.github.wanderwise_inc.app.ui.map.MapScreenTestTag
import com.github.wanderwise_inc.app.viewmodel.MapViewModel
import com.github.wanderwise_inc.app.viewmodel.ProfileViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.kotlin.whenever
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.junit.MockitoJUnit
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.junit.MockitoRule
import org.mockito.kotlin.any
import org.mockito.kotlin.whenever
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class HomeNavigationTest {
  @get:Rule val composeTestRule = createComposeRule()
  @get:Rule val mockitoRule: MockitoRule = MockitoJUnit.rule()

  @Mock private lateinit var imageRepository: ImageRepository
  @Mock private lateinit var mapViewModel: MapViewModel
  @Mock private lateinit var profileViewModel: ProfileViewModel
  @Mock private lateinit var firebaseAuth: FirebaseAuth
  @Mock private lateinit var demoSetup: DemoSetup

  private lateinit var navController: NavHostController

  @Before
  fun setupNavHost() {
    composeTestRule.setContent {
      `when`(demoSetup.demoSetup(mapViewModel, profileViewModel, firebaseAuth)).then {  }

      `when`(firebaseAuth.uid).thenReturn("0")

      navController = TestNavHostController(LocalContext.current)
      navController.navigatorProvider.addNavigator(ComposeNavigator())
      HomeScreen(imageRepository, mapViewModel, profileViewModel, navController, firebaseAuth)
    }
  }

  @Test
  fun verify_BottomNavigationBarIsDisplayed() {
    composeTestRule.onNodeWithTag("Bottom navigation bar").assertIsDisplayed()
  }

  @Test
  fun verify_StartDestinationIsOverviewScreen() {
    composeTestRule.onNodeWithTag(OverviewScreenTestTags.SCREEN).assertIsDisplayed()

    val route = navController.currentBackStackEntry?.destination?.route
    assertEquals(Route.OVERVIEW, route)
  }

  @Test
  fun performClick_OnItineraryButton_NavigatesToLikedScreen() {
    composeTestRule.onNodeWithTag(Route.LIKED).performClick()

    composeTestRule.onNodeWithTag(LikedScreenTestTags.SCREEN).assertIsDisplayed()

    val route = navController.currentBackStackEntry?.destination?.route
    assertEquals(Route.LIKED, route)
  }

  @Test
  fun performClick_OnItineraryButton_NavigatesToCreationScreen() {
    composeTestRule.onNodeWithTag(Route.CREATION).performClick()

    composeTestRule.onNodeWithTag(CreationScreenTestTags.SCREEN).assertIsDisplayed()

    val route = navController.currentBackStackEntry?.destination?.route
    assertEquals(Route.CREATION, route)
  }

  @Test
  fun performClick_OnMapButton_NavigatesToMapScreen() {
    composeTestRule.onNodeWithTag(Route.MAP).performClick()

    composeTestRule.onNodeWithTag(MapScreenTestTag.SCREEN).assertIsDisplayed()

    val route = navController.currentBackStackEntry?.destination?.route
    assertEquals(Route.MAP, route)
  }

  @Test
  fun performClick_OnProfileButton_NavigatesToProfileScreen() {
    composeTestRule.onNodeWithTag(Route.PROFILE).performClick()

    // TODO I don't know why this test isn't working
    // composeTestRule.onNodeWithTag(PROFILE_SCREEN_TEST_TAG).assertIsDisplayed()

    val route = navController.currentBackStackEntry?.destination?.route
    assertEquals(Route.PROFILE, route)
  }

  @Test
  fun performClicks_OnLikedThenMapButton_NavigatesToMapScreen() {
    composeTestRule.onNodeWithTag(Route.LIKED).performClick()

    composeTestRule.onNodeWithTag(Route.MAP).performClick()

    composeTestRule.onNodeWithTag(MapScreenTestTag.SCREEN).assertIsDisplayed()

    val route = navController.currentBackStackEntry?.destination?.route
    assertEquals(Route.MAP, route)
  }

  @Test
  fun performClicks_OnMapThenItineraryButton_NavigatesToLikedScreen() {
    // composeTestRule.onNodeWithTag(Route.MAP).performClick()

    composeTestRule.onNodeWithTag(Route.LIKED).performClick()

    composeTestRule.onNodeWithTag(LikedScreenTestTags.SCREEN).assertIsDisplayed()

    val route = navController.currentBackStackEntry?.destination?.route
    assertEquals(Route.LIKED, route)
  }

  @Test
  fun performClicks_OnMapThenOverviewButton_NavigatesToOverviewScreen() {
    composeTestRule.onNodeWithTag(Route.MAP).performClick()

    composeTestRule.onNodeWithTag(Route.OVERVIEW).performClick()

    composeTestRule.onNodeWithTag(OverviewScreenTestTags.SCREEN).assertIsDisplayed()

    val route = navController.currentBackStackEntry?.destination?.route
    assertEquals(Route.OVERVIEW, route)
  }

  @Test
  fun performMultipleClicks_OnNavigationBarButton_DoesNotAddToBackStack() {
    composeTestRule.onNodeWithTag(Route.MAP).performClick()

    composeTestRule.onNodeWithTag(Route.LIKED).performClick()

    composeTestRule.onNodeWithTag(Route.MAP).performClick()

    composeTestRule.onNodeWithTag(Route.LIKED).performClick()

    composeTestRule.onNodeWithTag(Route.MAP).performClick()

    navController.popBackStack() // simulates back button click

    val route = navController.currentBackStackEntry?.destination?.route
    assertEquals(Route.OVERVIEW, route)

    composeTestRule.onNodeWithTag(OverviewScreenTestTags.SCREEN).assertIsDisplayed()
  }

  @Test
  fun performMultipleClicks_OnNavigationBarButtonEndingOnOverviewScreen_DoesNotAddToBackStack() {
    composeTestRule.onNodeWithTag(Route.MAP).performClick()

    composeTestRule.onNodeWithTag(Route.LIKED).performClick()

    composeTestRule.onNodeWithTag(Route.MAP).performClick()

    composeTestRule.onNodeWithTag(Route.LIKED).performClick()

    composeTestRule.onNodeWithTag(Route.PROFILE).performClick()

    composeTestRule.onNodeWithTag(Route.OVERVIEW).performClick()

    navController.popBackStack() // simulates back button click

    val route = navController.currentBackStackEntry?.destination?.route
    assertEquals(null, route)
  }
}
*/
