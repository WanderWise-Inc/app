package com.github.wanderwise_inc.app.ui.navigation

import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.navigation.NavHostController
import androidx.navigation.compose.ComposeNavigator
import androidx.navigation.testing.TestNavHostController
import com.github.wanderwise_inc.app.ui.home.HomeScreen
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
      navController = TestNavHostController(LocalContext.current)
      navController.navigatorProvider.addNavigator(ComposeNavigator())
      HomeScreen(navController = navController)
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
  fun performClick_OnItineraryButton_NavigatesToItineraryScreen() {
    composeTestRule.onNodeWithTag(Route.ITINERARY + " button").performClick()

    composeTestRule.onNodeWithTag("Itinerary Screen").assertIsDisplayed()

    val route = navController.currentBackStackEntry?.destination?.route
    assertEquals(Route.ITINERARY, route)
  }

  @Test
  fun performClick_OnMapButton_NavigatesToMapScreen() {
    composeTestRule.onNodeWithText(Route.MAP).performClick()

    composeTestRule.onNodeWithTag("Map screen").assertIsDisplayed()

    val route = navController.currentBackStackEntry?.destination?.route
    assertEquals(Route.MAP, route)
  }

  @Test
  fun performClicks_OnItineraryThenMapButton_NavigatesToMapScreen() {
    composeTestRule.onNodeWithText(Route.ITINERARY).performClick()

    composeTestRule.onNodeWithText(Route.MAP).performClick()

    composeTestRule.onNodeWithTag("Map screen").assertIsDisplayed()

    val route = navController.currentBackStackEntry?.destination?.route
    assertEquals(Route.MAP, route)
  }

  @Test
  fun performClicks_OnMapThenItineraryButton_NavigatesToItineraryScreen() {
    composeTestRule.onNodeWithText(Route.MAP).performClick()

    composeTestRule.onNodeWithText(Route.ITINERARY).performClick()

    composeTestRule.onNodeWithTag("Itinerary Screen").assertIsDisplayed()

    val route = navController.currentBackStackEntry?.destination?.route
    assertEquals(Route.ITINERARY, route)
  }

  @Test
  fun performClicks_OnMapThenOverviewButton_NavigatesToOverviewScreen() {
    composeTestRule.onNodeWithText(Route.MAP).performClick()

    composeTestRule.onNodeWithText(Route.OVERVIEW).performClick()

    composeTestRule.onNodeWithTag("Overview screen").assertIsDisplayed()

    val route = navController.currentBackStackEntry?.destination?.route
    assertEquals(Route.OVERVIEW, route)
  }

  @Test
  fun performMultipleClicks_OnNavigationBarButton_DoesNotAddToBackStack() {
    composeTestRule.onNodeWithText(Route.MAP).performClick()

    composeTestRule.onNodeWithText(Route.ITINERARY).performClick()

    composeTestRule.onNodeWithText(Route.MAP).performClick()

    composeTestRule.onNodeWithText(Route.ITINERARY).performClick()

    composeTestRule.onNodeWithText(Route.MAP).performClick()

    navController.popBackStack() // simulates back button click

    val route = navController.currentBackStackEntry?.destination?.route
    assertEquals(Route.OVERVIEW, route)

    composeTestRule.onNodeWithTag("Overview screen").assertIsDisplayed()
  }

  @Test
  fun performMultipleClicks_OnNavigationBarButtonEndingOnOverviewScreen_DoesNotAddToBackStack() {
    composeTestRule.onNodeWithText(Route.MAP).performClick()

    composeTestRule.onNodeWithText(Route.ITINERARY).performClick()

    composeTestRule.onNodeWithText(Route.MAP).performClick()

    composeTestRule.onNodeWithText(Route.ITINERARY).performClick()

    composeTestRule.onNodeWithText(Route.OVERVIEW).performClick()

    navController.popBackStack() // simulates back button click

    val route = navController.currentBackStackEntry?.destination?.route
    assertEquals(null, route)
  }
}
