package com.github.wanderwise_inc.app.ui.navigation

import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.navigation.compose.ComposeNavigator
import androidx.navigation.testing.TestNavHostController
import com.github.wanderwise_inc.app.ui.navigation.graph.Graph
import com.github.wanderwise_inc.app.ui.navigation.graph.RootNavigationGraph
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.jupiter.api.Assertions.*
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner


@RunWith(RobolectricTestRunner::class)
class SignInNavigationTest {
    
    @get:Rule
    val composeTestRule = createComposeRule()
    //var activityScenarioRule = ActivityScenarioRule(MainActivity::class.java)
    
    private lateinit var navController: TestNavHostController
    
    @Before
    fun setupNavHost() {
        composeTestRule.setContent { 
            navController = TestNavHostController(LocalContext.current)
            navController.navigatorProvider.addNavigator(ComposeNavigator())
            RootNavigationGraph(navController = navController)
        }
    }
    
    @Test
    fun verify_StartDestinationIsSignInScreen() { 
        composeTestRule
            .onNodeWithTag("Sign in button")
            .assertIsDisplayed()
        
        val route = navController.currentBackStackEntry?.destination?.route
        assertEquals(Route.SIGNIN, route)
    }
    
    @Test
    fun performClick_OnSignInButton_navigatesToOverviewScreen() {
        composeTestRule
            .onNodeWithTag("Sign in button")
            .performClick()
        
        composeTestRule
            .onNodeWithTag("Overview screen")
            .assertIsDisplayed()
        
        val route = navController.currentBackStackEntry?.destination?.route
        assertEquals(Graph.HOME, route)
    }

    @Test
    fun performClick_OnSignInButton_ThenClickBackButton_navigatesToSignInScreen() {
        composeTestRule
            .onNodeWithTag("Sign in button")
            .performClick()
        
        navController.popBackStack() // simulates back button click

        composeTestRule
            .onNodeWithTag("Sign in button")
            .assertIsDisplayed()

        val route = navController.currentBackStackEntry?.destination?.route
        assertEquals(Route.SIGNIN, route)
    }
}