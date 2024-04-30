package com.github.wanderwise_inc.app.ui

import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.navigation.NavController
import androidx.navigation.compose.ComposeNavigator
import androidx.navigation.testing.TestNavHostController
import com.github.wanderwise_inc.app.model.profile.Profile
import com.github.wanderwise_inc.app.ui.navigation.graph.Graph
import com.github.wanderwise_inc.app.ui.signin.LoginScreen
import com.github.wanderwise_inc.app.viewmodel.ProfileViewModel
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.any
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class SignInButtonTest {

  @get:Rule val composeTestRule = createComposeRule()

  @MockK private lateinit var profileViewModel: ProfileViewModel
  private lateinit var navController: TestNavHostController

  @Before
  fun setup() {
    MockKAnnotations.init(this)
  }

  @Test
  fun testLoginScreenWithUserAlreadyPresentInDatabaseShouldNavigate() {
    // Launch activity with the NavHost and SignInButton composable

    val p = Profile("", "Test", "0", "Bio", null)

    coEvery { profileViewModel.getProfile(any()) } returns flow { emit(p) }

    // Perform actions and assert UI state using Espresso and MockK
    composeTestRule.setContent {
      navController = TestNavHostController(LocalContext.current)
      navController.navigatorProvider.addNavigator(ComposeNavigator())
      LoginScreen(profileViewModel, navController)
    }

    runBlocking { delay(2000) }

    navController.assertCurrentRouteName(Graph.AUTHENTICATION)

    runBlocking { delay(2000) }

    composeTestRule.onNodeWithText("Sign-In with Google").performClick()

    runBlocking { delay(2000) }

    navController.assertCurrentRouteName(Graph.HOME)

    runBlocking { delay(2000) }

    // Add assertions here based on the expected behavior of SignInButton
  }

  @Test fun testLoginScreenWhenUserNotPresentInDBShouldCallSetProfileAndNavigate() {}
}

fun NavController.assertCurrentRouteName(expectedRouteName: String) {
  Assert.assertEquals(expectedRouteName, currentBackStackEntry?.destination?.route)
}
