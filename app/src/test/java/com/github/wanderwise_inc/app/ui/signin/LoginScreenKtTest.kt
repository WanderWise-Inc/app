package com.github.wanderwise_inc.app.ui.signin

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.navigation.NavController
import com.github.wanderwise_inc.app.viewmodel.LoginViewModel
import com.github.wanderwise_inc.app.viewmodel.SignInState
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class LoginScreenKtTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @MockK
    private lateinit var loginViewModel: LoginViewModel

    @MockK
    private lateinit var navController: NavController

    private val _signInState = MutableLiveData(SignInState.NONE)
    private val signInState: LiveData<SignInState> get() = _signInState

    @Before
    fun setup() {
        _signInState.value = SignInState.NONE

        MockKAnnotations.init(this)

        every { loginViewModel.signInState } returns signInState

        composeTestRule.setContent { LoginScreen(loginViewModel, navController) }
        composeTestRule.waitForIdle()
    }

    @Test
    fun `initial elements are displayed correctly`() {

        composeTestRule.onNodeWithText("You can either wander dumb or,").assertExists()

        composeTestRule.onNodeWithText("WanderWise").assertExists()

        composeTestRule.onNodeWithContentDescription("new_logo_swent 1").assertExists()

        composeTestRule.onNodeWithText("Start Wandering Now").assertExists()

        composeTestRule.onNodeWithContentDescription("google-logo-9808 1").assertExists()

        composeTestRule.onNodeWithText("Sign-In with Google").assertExists()
    }
}
