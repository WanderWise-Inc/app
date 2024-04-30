package com.github.wanderwise_inc.app.ui

import android.content.Intent
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.navigation.NavHostController
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult
import com.github.wanderwise_inc.app.data.SignInRepositoryImpl
import com.github.wanderwise_inc.app.model.profile.Profile
import com.github.wanderwise_inc.app.ui.navigation.graph.Graph
import com.github.wanderwise_inc.app.ui.signin.LoginScreen
import com.github.wanderwise_inc.app.viewmodel.ProfileViewModel
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.every
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
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
  @MockK private lateinit var navController: NavHostController
  @MockK private lateinit var signInRepositoryImpl: SignInRepositoryImpl
  @MockK
  private lateinit var signInLauncher:
      ManagedActivityResultLauncher<Intent, FirebaseAuthUIAuthenticationResult>
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
    every { navController.navigate(Graph.HOME) } answers { route = Graph.HOME }
    // coEvery { signInRepositoryImpl.signIn(any(), any(), any(), any(), any()) } answers {route =
    // Graph.HOME}
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

    runBlocking { delay(10000) }

    assertEquals(Graph.HOME, route)
  }

  @Test
  fun testLoginScreenWhenUserNotPresentInDBShouldCallSetProfileAndNavigate() {
    coEvery { profileViewModel.getProfile(any()) } returns flow { emit(null) }
    every { navController.navigate(Graph.HOME) } answers { route = Graph.HOME }
    coEvery { profileViewModel.setProfile(any()) } answers { route = Graph.HOME }

    composeTestRule.setContent {
      LoginScreen(profileViewModel = profileViewModel, navController = navController)
    }

    composeTestRule.onNodeWithText("WanderWise").assertExists()
    composeTestRule.onNodeWithText("WanderWise").assertIsDisplayed()

    composeTestRule.onNodeWithText("Sign-In with Google").assertExists()
    composeTestRule.onNodeWithText("Sign-In with Google").assertIsDisplayed()

    runBlocking { delay(2000) }

    runBlocking { delay(2000) }

    composeTestRule.onNodeWithText("Sign-In with Google").performClick()

    runBlocking { delay(10000) }

    assertEquals(Graph.HOME, route)
  }
}
