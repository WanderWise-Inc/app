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
import com.github.wanderwise_inc.app.ui.liked.LikedScreen
import com.github.wanderwise_inc.app.viewmodel.MapViewModel
import com.google.firebase.FirebaseApp
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class LikedNavigationTest {
  @get:Rule val composeTestRule = createComposeRule()
  private lateinit var navController: NavHostController

  @Before
  fun setupNavHost() {
    composeTestRule.setContent {
      val itineraryRepository = ItineraryRepositoryTestImpl()
      val mapViewModel = MapViewModel(itineraryRepository)
      FirebaseApp.initializeApp(LocalContext.current)
      navController = TestNavHostController(LocalContext.current)
      navController.navigatorProvider.addNavigator(ComposeNavigator())
      LikedScreen(mapViewModel, navController)
    }
  }

  @Test
  fun verify_StartDestinationIsLikedAdventureScreen() {
    composeTestRule.onNodeWithTag("Liked Adventure screen").assertIsDisplayed()

    val route = navController.currentBackStackEntry?.destination?.route
    assertEquals(Route.LIKED + "/" + Tags.ADVENTURE, route)
  }

  @Test
  fun performClick_OnShoppingButton_NavigatesToLikedShoppingScreen() {
    composeTestRule.onNodeWithTag(Route.LIKED + "/" + Tags.SHOPPING).performClick()

    composeTestRule.onNodeWithTag("Liked Shopping screen").assertIsDisplayed()

    val route = navController.currentBackStackEntry?.destination?.route
    assertEquals(Route.LIKED + "/" + Tags.SHOPPING, route)
  }

  @Test
  fun performClick_OnSightseeingButton_NavigatesToLikedSightseeingScreen() {
    composeTestRule.onNodeWithTag(Route.LIKED + "/" + Tags.SIGHT_SEEING).performClick()

    composeTestRule.onNodeWithTag("Liked Sight-seeing screen").assertIsDisplayed()

    val route = navController.currentBackStackEntry?.destination?.route
    assertEquals(Route.LIKED + "/" + Tags.SIGHT_SEEING, route)
  }

  @Test
  fun performClick_OnDrinksButton_NavigatesToLikedDrinksScreen() {
    composeTestRule.onNodeWithTag(Route.LIKED + "/" + Tags.DRINKS).performClick()

    composeTestRule.onNodeWithTag("Liked Drinks screen").assertIsDisplayed()

    val route = navController.currentBackStackEntry?.destination?.route
    assertEquals(Route.LIKED + "/" + Tags.DRINKS, route)
  }

  /*@Test
  fun performClicks_OnLikedThenMapButton_NavigatesToMapScreen() {
      composeTestRule
          .onNodeWithTag(Route.LIKED)
          .performClick()

      composeTestRule
          .onNodeWithTag(Route.MAP)
          .performClick()

      composeTestRule
          .onNodeWithTag("Map screen")
          .assertIsDisplayed()

      val route = navController.currentBackStackEntry?.destination?.route
      assertEquals(Route.MAP, route)
  }

  @Test
  fun performClicks_OnMapThenItineraryButton_NavigatesToLikedScreen() {
      composeTestRule
          .onNodeWithTag(Route.MAP)
          .performClick()

      composeTestRule
          .onNodeWithTag(Route.LIKED)
          .performClick()

      composeTestRule
          .onNodeWithTag("Liked screen")
          .assertIsDisplayed()

      val route = navController.currentBackStackEntry?.destination?.route
      assertEquals(Route.LIKED, route)
  }

  @Test
  fun performClicks_OnMapThenOverviewButton_NavigatesToOverviewScreen() {
      composeTestRule
          .onNodeWithTag(Route.MAP)
          .performClick()

      composeTestRule
          .onNodeWithTag(Route.OVERVIEW)
          .performClick()

      composeTestRule
          .onNodeWithTag("Overview screen")
          .assertIsDisplayed()

      val route = navController.currentBackStackEntry?.destination?.route
      assertEquals(Route.OVERVIEW, route)
  }

  @Test
  fun performMultipleClicks_OnNavigationBarButton_DoesNotAddToBackStack() {
      composeTestRule
          .onNodeWithTag(Route.MAP)
          .performClick()

      composeTestRule
          .onNodeWithTag(Route.LIKED)
          .performClick()

      composeTestRule
          .onNodeWithTag(Route.MAP)
          .performClick()

      composeTestRule
          .onNodeWithTag(Route.LIKED)
          .performClick()

      composeTestRule
          .onNodeWithTag(Route.MAP)
          .performClick()

      navController.popBackStack() // simulates back button click

      val route = navController.currentBackStackEntry?.destination?.route
      assertEquals(Route.OVERVIEW, route)

      composeTestRule
          .onNodeWithTag("Overview screen")
          .assertIsDisplayed()
  }

  @Test
  fun performMultipleClicks_OnNavigationBarButtonEndingOnOverviewScreen_DoesNotAddToBackStack() {
      composeTestRule
          .onNodeWithTag(Route.MAP)
          .performClick()

      composeTestRule
          .onNodeWithTag(Route.LIKED)
          .performClick()

      composeTestRule
          .onNodeWithTag(Route.MAP)
          .performClick()

      composeTestRule
          .onNodeWithTag(Route.LIKED)
          .performClick()

      composeTestRule
          .onNodeWithTag(Route.OVERVIEW)
          .performClick()

      navController.popBackStack() // simulates back button click

      val route = navController.currentBackStackEntry?.destination?.route
      assertEquals(null, route)
  }*/
}
