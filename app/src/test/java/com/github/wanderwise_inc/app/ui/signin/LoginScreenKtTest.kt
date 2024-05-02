package com.github.wanderwise_inc.app.ui.signin

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.github.wanderwise_inc.app.data.GoogleSignInLauncher
import com.github.wanderwise_inc.app.ui.navigation.graph.Graph
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import org.junit.Assert.*
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class LoginScreenKtTest {

  @get:Rule val composeTestRule = createComposeRule()

  private var route = Graph.AUTHENTICATION

  @MockK private lateinit var googleSignInLauncher: MockGoogleSignInLauncher

  @Before
  fun setup() {
    MockKAnnotations.init(this)
    composeTestRule.setContent { LoginScreen(googleSignInLauncher) }
    composeTestRule.waitForIdle()
  }

  @Test
  fun testLoginScreenWithUserAlreadyPresentInDatabaseShouldNavigate() {
    every { googleSignInLauncher.launchSignIn() } answers { route = Graph.HOME }

    composeTestRule.onNodeWithText("WanderWise").assertExists()
    composeTestRule.onNodeWithText("WanderWise").assertIsDisplayed()

    composeTestRule.onNodeWithText("Sign-In with Google").assertExists()
    // composeTestRule.onNodeWithText("Sign-In with Google").assertIsDisplayed()

    composeTestRule.onNodeWithText("Sign-In with Google").performClick()

    assertEquals(Graph.HOME, route)
  }
}

class MockGoogleSignInLauncher : GoogleSignInLauncher {
  override fun launchSignIn() {
    TODO("Not yet implemented")
  }
}
