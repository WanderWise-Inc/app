/*
package com.github.wanderwise_inc.app.ui.signin

import android.app.Application
import android.content.Context
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.navigation.NavHostController
import androidx.navigation.compose.ComposeNavigator
import androidx.navigation.testing.TestNavHostController
import androidx.test.core.app.ApplicationProvider
import com.github.wanderwise_inc.app.data.ImageRepository
import com.github.wanderwise_inc.app.data.ImageRepositoryTestImpl
import com.github.wanderwise_inc.app.data.ProfileRepository
import com.github.wanderwise_inc.app.data.ProfileRepositoryTestImpl
import com.github.wanderwise_inc.app.viewmodel.ProfileViewModel
import com.google.firebase.FirebaseApp
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class TestLoginScreen {
  @get:Rule val composeTestRule = createComposeRule()

  private lateinit var navController: NavHostController
  private lateinit var profileRepository: ProfileRepository
  private lateinit var imageRepository: ImageRepository
  private lateinit var profileViewModel: ProfileViewModel
  private lateinit var context: Context

  @Mock private lateinit var mockApplication: Application

  @Before
  fun setup() {
    val application = ApplicationProvider.getApplicationContext<Application>()
    context = application
    if (context != null) {
      FirebaseApp.initializeApp(context)
    }
    navController = TestNavHostController(application)
    navController.navigatorProvider.addNavigator(ComposeNavigator())
    profileRepository = ProfileRepositoryTestImpl()
    imageRepository = ImageRepositoryTestImpl(application)
    profileViewModel = ProfileViewModel(profileRepository, imageRepository)

    composeTestRule.setContent {
      LoginScreen(
        profileViewModel = profileViewModel,
        navController = navController
      )
    }
  }

  @Test
  fun dummy() {
    assert(true)
  }
}
*/
