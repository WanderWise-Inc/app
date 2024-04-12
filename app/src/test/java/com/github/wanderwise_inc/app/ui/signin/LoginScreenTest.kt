package com.github.wanderwise_inc.app.ui.signin

import android.content.Context
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.navigation.NavHostController
import com.github.wanderwise_inc.app.viewmodel.ProfileViewModel
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito

class LoginScreenTest {

  @get:Rule val composeTestRule = createComposeRule()

  @Test
  fun testLoginScreenVisuals() {
    // Mock dependencies
    val mockProfileViewModel = Mockito.mock(ProfileViewModel::class.java)
    val mockNavController = Mockito.mock(NavHostController::class.java)
    val mockContext = Mockito.mock(Context::class.java)

    // Mock android.os.Build.FINGERPRINT to prevent NullPointerException
    Mockito.`when`(mockContext.getSystemService(Context.WINDOW_SERVICE)).thenReturn(mockContext)

    // Set up content
    composeTestRule.setContent {
      LoginScreen(
          context = mockContext,
          profileViewModel = mockProfileViewModel,
          navController = mockNavController)
    }

    // Assert that the "Sign-In with Google" button is displayed
    composeTestRule.onNodeWithText("Sign-In with Google").assertExists()

    // Assert that the "WanderWise" text is displayed
    composeTestRule.onNodeWithText("WanderWise").assertExists()

    // Add more visual assertions here as needed
  }
}
