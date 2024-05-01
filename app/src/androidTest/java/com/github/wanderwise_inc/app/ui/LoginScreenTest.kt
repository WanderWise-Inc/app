package com.github.wanderwise_inc.app.ui

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.github.wanderwise_inc.app.data.GoogleSignInLauncher
import com.github.wanderwise_inc.app.ui.navigation.graph.Graph
import com.github.wanderwise_inc.app.ui.signin.LoginScreen
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class SignInButtonTest {

  @get:Rule val composeTestRule = createComposeRule()

  private var route = Graph.AUTHENTICATION

  @MockK private lateinit var googleSignInLauncher: MockGoogleSignInLauncher

  @Before
  fun setup() {
    MockKAnnotations.init(this)
  }

  @Test
  fun testLoginScreenWithUserAlreadyPresentInDatabaseShouldNavigate() {
    every { googleSignInLauncher.launchSignIn() } answers { route = Graph.HOME }
    composeTestRule.setContent { LoginScreen(googleSignInLauncher) }

    composeTestRule.onNodeWithText("WanderWise").assertExists()
    composeTestRule.onNodeWithText("WanderWise").assertIsDisplayed()

    composeTestRule.onNodeWithText("Sign-In with Google").assertExists()
    composeTestRule.onNodeWithText("Sign-In with Google").assertIsDisplayed()

    runBlocking { delay(2000) }

    composeTestRule.onNodeWithText("Sign-In with Google").performClick()

    runBlocking { delay(2000) }

    assertEquals(Graph.HOME, route)
  }
}

class MockGoogleSignInLauncher : GoogleSignInLauncher {
  override fun launchSignIn() {
    TODO("Not yet implemented")
  }
}
