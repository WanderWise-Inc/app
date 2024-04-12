package com.github.wanderwise_inc.app.ui.navigation

import org.junit.jupiter.api.Assertions.*


/*
@RunWith(RobolectricTestRunner::class)
class SignInNavigationTest {

    @get:Rule
    val composeTestRule = createComposeRule()
    //var activityScenarioRule = ActivityScenarioRule(MainActivity::class.java)

    private lateinit var navController: TestNavHostController

    Before
    fun setupNavHost() {
        composeTestRule.setContent {
            // val userViewModel by viewModels<UserViewModel>()
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

     */
