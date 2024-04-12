package com.github.wanderwise_inc.app.ui.navigation

import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.navigation.NavHostController
import androidx.navigation.compose.ComposeNavigator
import androidx.navigation.testing.TestNavHostController
import com.github.wanderwise_inc.app.data.ItineraryRepositoryTestImpl
import com.github.wanderwise_inc.app.ui.home.HomeScreen
import com.github.wanderwise_inc.app.viewmodel.HomeViewModel
import com.github.wanderwise_inc.app.viewmodel.MapViewModel
import com.google.firebase.FirebaseApp
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class HomeNavigationTest {
  @get:Rule val composeTestRule = createComposeRule()
  private lateinit var navController: NavHostController

  @Before
  fun setupNavHost() {
    composeTestRule.setContent {
      val homeViewModel = HomeViewModel()
      val itineraryRepository = ItineraryRepositoryTestImpl()
      val mapViewModel = MapViewModel(itineraryRepository)
      FirebaseApp.initializeApp(LocalContext.current)
      navController = TestNavHostController(LocalContext.current)
      navController.navigatorProvider.addNavigator(ComposeNavigator())
      HomeScreen(homeViewModel, mapViewModel, navController)
    }
  }

  @Test
  fun verify_BottomNavigationBarIsDisplayed() {
    composeTestRule.onNodeWithTag("Bottom navigation bar").assertIsDisplayed()
  }

  @Test
  fun verify_StartDestinationIsOverviewScreen() {
    composeTestRule.onNodeWithTag("Overview screen").assertIsDisplayed()

    val route = navController.currentBackStackEntry?.destination?.route
    assertEquals(Route.OVERVIEW, route)
  }

  @Test
  fun performClick_OnItineraryButton_NavigatesToLikedScreen() {
    composeTestRule.onNodeWithTag(Route.LIKED).performClick()

    composeTestRule.onNodeWithTag("Liked screen").assertIsDisplayed()

    val route = navController.currentBackStackEntry?.destination?.route
    assertEquals(Route.LIKED, route)
  }

  @Test
  fun performClick_OnItineraryButton_NavigatesToSearchScreen() {
    composeTestRule.onNodeWithTag(Route.SEARCH).performClick()

    composeTestRule.onNodeWithTag("Search screen").assertIsDisplayed()

    val route = navController.currentBackStackEntry?.destination?.route
    assertEquals(Route.SEARCH, route)
  }

  @Test
  fun performClick_OnMapButton_NavigatesToMapScreen() {
    composeTestRule.onNodeWithTag(Route.MAP).performClick()

    composeTestRule.onNodeWithTag("Map screen").assertIsDisplayed()

    val route = navController.currentBackStackEntry?.destination?.route
    assertEquals(Route.MAP, route)
  }

  @Test
  fun performClick_OnProfileButton_NavigatesToProfileScreen() {
    composeTestRule.onNodeWithTag(Route.PROFILE).performClick()

    composeTestRule.onNodeWithTag("Profile screen").assertIsDisplayed()

    val route = navController.currentBackStackEntry?.destination?.route
    assertEquals(Route.PROFILE, route)
  }

  @Test
  fun performClicks_OnLikedThenMapButton_NavigatesToMapScreen() {
    composeTestRule.onNodeWithTag(Route.LIKED).performClick()

    composeTestRule.onNodeWithTag(Route.MAP).performClick()

    composeTestRule.onNodeWithTag("Map screen").assertIsDisplayed()

    val route = navController.currentBackStackEntry?.destination?.route
    assertEquals(Route.MAP, route)
  }

  @Test
  fun performClicks_OnMapThenItineraryButton_NavigatesToLikedScreen() {
    composeTestRule.onNodeWithTag(Route.MAP).performClick()

    composeTestRule.onNodeWithTag(Route.LIKED).performClick()

    composeTestRule.onNodeWithTag("Liked screen").assertIsDisplayed()

    val route = navController.currentBackStackEntry?.destination?.route
    assertEquals(Route.LIKED, route)
  }

  @Test
  fun performClicks_OnMapThenOverviewButton_NavigatesToOverviewScreen() {
    composeTestRule.onNodeWithTag(Route.MAP).performClick()

    composeTestRule.onNodeWithTag(Route.OVERVIEW).performClick()

    composeTestRule.onNodeWithTag("Overview screen").assertIsDisplayed()

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

    composeTestRule.onNodeWithTag("Overview screen").assertIsDisplayed()
  }

  @Test
  fun performMultipleClicks_OnNavigationBarButtonEndingOnOverviewScreen_DoesNotAddToBackStack() {
    composeTestRule.onNodeWithTag(Route.MAP).performClick()

    composeTestRule.onNodeWithTag(Route.LIKED).performClick()

    composeTestRule.onNodeWithTag(Route.MAP).performClick()

    composeTestRule.onNodeWithTag(Route.LIKED).performClick()

    composeTestRule.onNodeWithTag(Route.OVERVIEW).performClick()

    navController.popBackStack() // simulates back button click

    val route = navController.currentBackStackEntry?.destination?.route
    assertEquals(null, route)
  }
}
