package com.github.wanderwise_inc.app.ui.signin

import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.navigation.NavController
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.wanderwise_inc.app.ui.TestTags
import com.github.wanderwise_inc.app.viewmodel.LoginViewModel
import com.github.wanderwise_inc.app.viewmodel.SignInState
import io.mockk.MockKAnnotations
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.impl.annotations.MockK
import org.junit.After
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LoginScreenTest {

  @get:Rule val composeTestRule = createComposeRule()

  @MockK private lateinit var loginViewModel: LoginViewModel

  @MockK private lateinit var navController: NavController

  private val _signInState = MutableLiveData<SignInState>()
  private val signInState: LiveData<SignInState>
    get() = _signInState

  @Before
  fun setup() {
    _signInState.value = SignInState.NONE

    MockKAnnotations.init(this)

    every { loginViewModel.signInState } returns signInState

    composeTestRule.setContent { LoginScreen(loginViewModel, navController) }
    composeTestRule.waitForIdle()
  }

  @After
  fun tearDown() {
    _signInState.value = SignInState.NONE
    clearAllMocks()
  }

  @Test
  fun `initial elements are displayed correctly`() {

    composeTestRule
        .onNodeWithText(text = "You can either wander dumb or,", useUnmergedTree = true)
        .assertIsDisplayed()

    composeTestRule.onNodeWithText(text = "WanderWise", useUnmergedTree = true).assertIsDisplayed()

    composeTestRule
        .onNodeWithContentDescription(label = "WanderWise logo", useUnmergedTree = true)
        .assertIsDisplayed()

    composeTestRule
        .onNodeWithText(text = "Start Wandering Now", useUnmergedTree = true)
        .assertIsDisplayed()

    composeTestRule
        .onNodeWithContentDescription(label = "Google logo", useUnmergedTree = true)
        .assertIsDisplayed()

    composeTestRule
        .onNodeWithText(text = "Sign-In with Google", useUnmergedTree = true)
        .assertIsDisplayed()

    composeTestRule
        .onNodeWithTag(testTag = TestTags.SIGN_IN_BUTTON, useUnmergedTree = true)
        .assertHasClickAction()
  }

  @Test
  fun `clicking on sign in button should navigate only when sign in is successful`() {
    var hasNavigated = false

    every { navController.navigate(any<String>()) } answers { hasNavigated = true }

    every { loginViewModel.signIn() } answers
        {
          _signInState.value = SignInState.FAILURE
        } andThenAnswer
        {
          _signInState.value = SignInState.SUCCESS
        }

    composeTestRule
        .onNodeWithTag(testTag = TestTags.SIGN_IN_BUTTON, useUnmergedTree = true)
        .performClick()
    composeTestRule.waitForIdle()

    assertFalse(hasNavigated)

    composeTestRule
        .onNodeWithTag(testTag = TestTags.SIGN_IN_BUTTON, useUnmergedTree = true)
        .performClick()
    composeTestRule.waitForIdle()

    assertTrue(hasNavigated)
  }
}
