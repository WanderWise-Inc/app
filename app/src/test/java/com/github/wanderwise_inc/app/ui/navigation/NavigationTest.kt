package com.github.wanderwise_inc.app.ui.navigation

import androidx.activity.ComponentActivity
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.navigation.compose.ComposeNavigator
import androidx.navigation.testing.TestNavHostController
import androidx.test.core.app.ApplicationProvider
import com.github.wanderwise_inc.app.ui.navigation.graph.Graph
import com.github.wanderwise_inc.app.ui.navigation.graph.RootNavigationGraph
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.jupiter.api.Assertions.*
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner


@RunWith(RobolectricTestRunner::class)
class NavigationTest {
    
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
    fun verifyStartDestinationIsSignInScreen() { 
        composeTestRule
            .onNodeWithText("Sign in")
            .assertIsDisplayed()
        
        val route = navController.currentBackStackEntry?.destination?.route
        assertEquals(route, Graph.AUTHENTICATION)
    }
    
    @Test
    fun verifySignInLeadsToOverviewScreen() {
        composeTestRule
            .onNodeWithText("Sign In")
            .performClick()
        
        val route = navController.currentBackStackEntry?.destination?.route
        assertEquals(route, Route.OVERVIEW)
    }
}