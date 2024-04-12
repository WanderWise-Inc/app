package com.github.wanderwise_inc.app.ui.navigation

import OverviewScreen
import androidx.activity.viewModels
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.navigation.NavHostController
import androidx.navigation.compose.ComposeNavigator
import androidx.navigation.testing.TestNavHostController
import androidx.test.platform.app.InstrumentationRegistry
import com.github.wanderwise_inc.app.data.ItineraryRepositoryTestImpl
import com.github.wanderwise_inc.app.ui.home.HomeScreen
import com.github.wanderwise_inc.app.viewmodel.HomeViewModel
import com.github.wanderwise_inc.app.viewmodel.MapViewModel
import com.github.wanderwise_inc.app.viewmodel.UserViewModel
import com.google.firebase.FirebaseApp
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.extension.TestTemplateInvocationContextProvider
import org.junit.runner.RunWith
import org.mockito.Mock
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class OverviewNavigationTest {    
    @get:Rule
    val composeTestRule = createComposeRule()
    private lateinit var navController: NavHostController

    
    @Before
    fun setupNavHost() {
        composeTestRule.setContent {
            val itineraryRepository = ItineraryRepositoryTestImpl()
            val mapViewModel = MapViewModel(itineraryRepository)
            FirebaseApp.initializeApp(LocalContext.current)
            navController = TestNavHostController(LocalContext.current)
            navController.navigatorProvider.addNavigator(ComposeNavigator())
            OverviewScreen(mapViewModel, navController)
        }
    }
    
    @Test
    fun verify_StartDestinationIsOverviewTrendingScreen() {
        composeTestRule
            .onNodeWithTag("Overview Trending screen")
            .assertIsDisplayed()
        
        val route = navController.currentBackStackEntry?.destination?.route
        assertEquals(Route.OVERVIEW + "/" + Tags.TRENDING, route)
    }
    
    @Test
    fun performClick_OnAdventureButton_NavigatesToOverviewAdventureScreen() {
        composeTestRule
            .onNodeWithTag(Route.OVERVIEW + "/" + Tags.ADVENTURE)
            .performClick()
        
        composeTestRule
            .onNodeWithTag("Overview Adventure screen")
            .assertIsDisplayed()
        
        val route = navController.currentBackStackEntry?.destination?.route
        assertEquals(Route.OVERVIEW + "/" + Tags.ADVENTURE, route)
    }

    @Test
    fun performClick_OnShoppingButton_NavigatesToOverviewShoppingScreen() {
        composeTestRule
            .onNodeWithTag(Route.OVERVIEW + "/" + Tags.SHOPPING)
            .performClick()

        composeTestRule
            .onNodeWithTag("Overview Shopping screen")
            .assertIsDisplayed()

        val route = navController.currentBackStackEntry?.destination?.route
        assertEquals(Route.OVERVIEW + "/" + Tags.SHOPPING, route)
    }

    @Test
    fun performClick_OnSightseeingButton_NavigatesToOverviewSightseeingScreen() {
        composeTestRule
            .onNodeWithTag(Route.OVERVIEW + "/" + Tags.SIGHT_SEEING)
            .performClick()

        composeTestRule
            .onNodeWithTag("Overview Sight-seeing screen")
            .assertIsDisplayed()

        val route = navController.currentBackStackEntry?.destination?.route
        assertEquals(Route.OVERVIEW + "/" + Tags.SIGHT_SEEING, route)
    }

    @Test
    fun performClick_OnDrinksButton_NavigatesToOverviewDrinksScreen() {
        composeTestRule
            .onNodeWithTag(Route.OVERVIEW + "/" + Tags.DRINKS)
            .performClick()

        composeTestRule
            .onNodeWithTag("Overview Drinks screen")
            .assertIsDisplayed()

        val route = navController.currentBackStackEntry?.destination?.route
        assertEquals(Route.OVERVIEW + "/" + Tags.DRINKS, route)
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