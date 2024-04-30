package com.github.wanderwise_inc.app.ui

import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.ComposeNavigator
import androidx.navigation.testing.TestNavHostController
import com.github.wanderwise_inc.app.data.SignInRepositoryImpl
import com.github.wanderwise_inc.app.model.profile.Profile
import com.github.wanderwise_inc.app.ui.navigation.graph.Graph
import com.github.wanderwise_inc.app.ui.signin.LoginScreen
import com.github.wanderwise_inc.app.viewmodel.ProfileViewModel
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.just
import io.mockk.runs
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.any
import org.mockito.Mockito.anyString
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class SignInButtonTest {

  @get:Rule val composeTestRule = createComposeRule()

  @MockK private lateinit var profileViewModel: ProfileViewModel
  @MockK private lateinit var navController: NavHostController
  @MockK private lateinit var signInRepositoryImpl: SignInRepositoryImpl
  private var route = Graph.AUTHENTICATION

  @Before
  fun setup() {
    MockKAnnotations.init(this)
  }

  @Test
  fun testLoginScreenWithUserAlreadyPresentInDatabaseShouldNavigate() {
    // Launch activity with the NavHost and SignInButton composable

    val p = Profile("", "Test", "0", "Bio", null)

    coEvery { profileViewModel.getProfile(any()) } returns flow { emit(p) }
    coEvery { signInRepositoryImpl.signIn(any(), any(), any(), any(), any()) } answers {route = Graph.HOME}
    // Perform actions and assert UI state using Espresso and MockK
    composeTestRule.setContent {
      LoginScreen(profileViewModel = profileViewModel, navController = navController)
    }

    composeTestRule.onNodeWithText("WanderWise").assertExists()
    composeTestRule.onNodeWithText("WanderWise").assertIsDisplayed()

    composeTestRule.onNodeWithText("Sign-In with Google").assertExists()
    composeTestRule.onNodeWithText("Sign-In with Google").assertIsDisplayed()

    runBlocking { delay(2000) }

    composeTestRule.onNodeWithText("Sign-In with Google").performClick()

    assertEquals(Graph.HOME, route)

    runBlocking {
      delay(2000)
    }
  }

  @Test fun testLoginScreenWhenUserNotPresentInDBShouldCallSetProfileAndNavigate() {}
}

fun NavController.assertCurrentRouteName(expectedRouteName: String) {
  Assert.assertEquals(expectedRouteName, currentBackStackEntry?.destination?.route)
}
