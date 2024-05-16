package com.github.wanderwise_inc.app.data

import androidx.navigation.NavController
import com.github.wanderwise_inc.app.model.profile.Profile
import com.github.wanderwise_inc.app.ui.navigation.graph.Graph
import com.github.wanderwise_inc.app.viewmodel.ProfileViewModel
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.fail
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.anyString
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnit
import org.mockito.junit.MockitoRule
import org.mockito.kotlin.verify
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class SignInRepositoryTest {

  @get:Rule val mockitoRule: MockitoRule = MockitoJUnit.rule()
  @Mock private lateinit var mockedUser: FirebaseUser
  @Mock private lateinit var navController: NavController
  @Mock private lateinit var profileViewModel: ProfileViewModel

  private lateinit var signInRepositoryImpl: SignInRepositoryImpl

  @Before
  fun setup() {
    signInRepositoryImpl = SignInRepositoryImpl()
  }

  @Test
  fun `if user is null, no exception should be thrown`() = runTest {
    try {
      signInRepositoryImpl.signIn(navController, profileViewModel, null)
    } catch (e: Exception) {
      fail("signIn should not throw. Exception was $e")
    }
  }

  @Test
  fun `if user already in database then just navigate`() = runTest {
    val profile = Profile("Test", "0", "Bio")
    val flow = flow<Profile?> { emit(profile) }
    var path = "begin"
    `when`(mockedUser.uid).thenReturn("0")
    `when`(profileViewModel.getProfile(anyString())).thenReturn(flow)
    `when`(navController.navigate(Graph.HOME)).then {
      path = "HOME"
      null
    }

    signInRepositoryImpl.signIn(navController, profileViewModel, mockedUser)

    assertEquals("HOME", path)
  }

  @Test
  fun `if user not in database, then it will be added and navigate`() = runTest {
    val flow = flow<Profile?> { emit(null) }
    `when`(profileViewModel.getProfile(anyString())).thenReturn(flow)
    `when`(mockedUser.uid).thenReturn("0")
    `when`(mockedUser.displayName).thenReturn("Test")
    `when`(mockedUser.photoUrl).thenReturn(null)
    val p = Profile("Test", "0", "")

    var path = "test"
    `when`(navController.navigate(Graph.HOME)).then {
      path = "HOME"
      null
    }

    signInRepositoryImpl.signIn(navController, profileViewModel, mockedUser)

    verify(profileViewModel).setProfile(p)
    verify(navController).navigate(Graph.HOME)
    assertEquals("HOME", path)
  }
}
