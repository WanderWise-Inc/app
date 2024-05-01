package com.github.wanderwise_inc.app.ui

/*

@RunWith(MockitoJUnitRunner::class)
class LoginScreenTest {

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
  override fun launchSignIn() {}
}
*/
