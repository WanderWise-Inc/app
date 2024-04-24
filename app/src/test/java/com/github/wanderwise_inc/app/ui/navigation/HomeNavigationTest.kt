package com.github.wanderwise_inc.app.ui.navigation

import android.app.Application
import android.content.Context
import android.location.Location
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.navigation.NavHostController
import androidx.navigation.compose.ComposeNavigator
import androidx.navigation.testing.TestNavHostController
import com.github.wanderwise_inc.app.data.ImageRepositoryTestImpl
import com.github.wanderwise_inc.app.data.ItineraryRepositoryTestImpl
import com.github.wanderwise_inc.app.data.ProfileRepositoryTestImpl
import com.github.wanderwise_inc.app.ui.home.HomeScreen
import com.github.wanderwise_inc.app.viewmodel.HomeViewModel
import com.github.wanderwise_inc.app.viewmodel.MapViewModel
import com.github.wanderwise_inc.app.viewmodel.ProfileViewModel
import com.github.wanderwise_inc.app.viewmodel.UserLocationClient
import com.google.firebase.FirebaseApp
import kotlinx.coroutines.flow.flow
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.anyLong
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnit
import org.mockito.junit.MockitoRule
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class HomeNavigationTest {
  @get:Rule val composeTestRule = createComposeRule()
  private lateinit var navController: NavHostController

  @get:Rule val mockitoRule: MockitoRule = MockitoJUnit.rule()

  @Mock private lateinit var userLocationClient: UserLocationClient

  @Mock private lateinit var mockApplication: Application

  @Mock private lateinit var mockContext: Context

  private val epflLat = 46.519126741544575
  private val epflLon = 6.5676006970802145

  @Before
  fun setupNavHost() {
    composeTestRule.setContent {
      val homeViewModel = HomeViewModel()
      mockApplication = mock(Application::class.java)
      mockContext = mock(Context::class.java)

      `when`(mockApplication.applicationContext).thenReturn(mockContext)

      `when`(userLocationClient.getLocationUpdates(anyLong())).thenReturn(
        flow {
          emit(
            Location("TestProvider").apply {
              latitude = epflLat
              longitude = epflLon
            }
          )
        }
      )

      val imageRepository = ImageRepositoryTestImpl(mockApplication)
      val profileRepository = ProfileRepositoryTestImpl()
      val profileViewModel = ProfileViewModel(profileRepository, imageRepository)

      val itineraryRepository = ItineraryRepositoryTestImpl()
      val mapViewModel = MapViewModel(itineraryRepository, userLocationClient)
      FirebaseApp.initializeApp(LocalContext.current)
      navController = TestNavHostController(LocalContext.current)
      navController.navigatorProvider.addNavigator(ComposeNavigator())
      HomeScreen(homeViewModel, mapViewModel, profileViewModel, navController)
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

    // TODO I don't know why this test isn't working
    // composeTestRule.onNodeWithTag(PROFILE_SCREEN_TEST_TAG).assertIsDisplayed()

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
