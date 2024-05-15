package com.github.wanderwise_inc.app.data

import android.content.Intent
import androidx.activity.result.ActivityResultLauncher
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnit
import org.mockito.junit.MockitoRule
import org.mockito.kotlin.verify
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class GoogleSignInLauncherTest {
  @get:Rule val mockitoRule: MockitoRule = MockitoJUnit.rule()

  private lateinit var googleSignInLauncher: DefaultGoogleSignInLauncher
  @Mock private lateinit var signInLauncher: ActivityResultLauncher<Intent>
  @Mock private lateinit var signInIntent: Intent

  @Before
  fun setup() {
    googleSignInLauncher = DefaultGoogleSignInLauncher(signInLauncher, signInIntent)
  }

  @Test
  fun `googleSignInLauncher should launch sign in`() {
    googleSignInLauncher.signIn()
    verify(signInLauncher).launch(signInIntent)
  }
}
