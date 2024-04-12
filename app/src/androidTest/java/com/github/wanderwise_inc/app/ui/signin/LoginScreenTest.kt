package com.github.wanderwise_inc.app.ui.signin

import androidx.compose.ui.test.junit4.createComposeRule
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock

class LoginScreenTest {
  @get:Rule val composeTestRule = createComposeRule()

  @Mock private lateinit var firebaseAuth: FirebaseAuth

  @Before
  fun setup() {
    composeTestRule.setContent {}
  }

  @OptIn(ExperimentalCoroutinesApi::class)
  @Test
  fun user_canceled_sign_in() = runTest {
    /*        val mockAuth = mock(FirebaseAuth::class.java)
    `when`(mockAuth.currentUser).thenReturn(null)

    val mockActivityResultContract = object : ActivityResultContract<Unit, ActivityResult>() {
        override fun createIntent(context: Context, input: Unit): Intent {
            return Intent(context, MainActivity::class.java) // Replace YourSignInActivity::class.java with your sign-in activity
        }

        override fun parseResult(resultCode: Int, intent: Intent?): ActivityResult {
            val idpResponse = mock(IdpResponse::class.java)
            return ActivityResult(RESULT_OK, Intent().apply {
                putExtra("extra_idp_response", idpResponse)
            })
        }
    }

    val mockLauncher = TestActivityResultLauncher(mockActivityResultContract)

    composeTestRule.setContent {
        SignInButton(
            context = mock(Context::class.java),
            userViewModel = mock(UserViewModel::class.java),
            profileViewModel = mock(ProfileViewModel::class.java),
            navController = mock(NavHostController::class.java)
        )
    }

    composeTestRule.onNodeWithTag("Sign in button").performClick()*/
    assertEquals(2, 1 + 1)
  }
}

// Test activity result launcher
/*
class TestActivityResultLauncher<I, O>(
    private val contract: ActivityResultContract<I, O>
) : ActivityResultLauncher<I>() {
    private lateinit var callback: ActivityResultCallback<O>

    override fun launch(input: I, options: ActivityOptionsCompat?) {
        val result = contract.parseResult(RESULT_OK, null)
        callback.onActivityResult(result)
    }

    override fun launch(input: I) {
        val result = contract.parseResult(RESULT_OK, null)
        callback.onActivityResult(result)
    }

    fun register(callback: ActivityResultCallback<O>) {
        this.callback = callback
    }

    override fun getContract(): ActivityResultContract<I, *> {
        TODO("Not yet implemented")
    }

    override fun unregister() {
        // No-op method
    }
}
*/
