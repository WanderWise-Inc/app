package com.github.wanderwise_inc.app.data

import android.content.Intent
import androidx.activity.result.ActivityResultLauncher
import io.mockk.MockKAnnotations
import io.mockk.impl.annotations.MockK
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnit
import org.mockito.junit.MockitoRule
import org.mockito.kotlin.any
import org.mockito.kotlin.verify
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class SignInLauncherTest {
    @get:Rule
    val mockitoRule: MockitoRule = MockitoJUnit.rule()

    private lateinit var googleSignInLauncher: GoogleSignInLauncher

    @MockK
    private lateinit var signInLauncher: ActivityResultLauncher<Intent>

    @Before
    fun setup() {
        MockKAnnotations.init(this)
        googleSignInLauncher = GoogleSignInLauncher(signInLauncher)
    }

    @Test
    fun `googleSignInLauncher should launch sign in`() {
        googleSignInLauncher.signIn()
        verify { signInLauncher.launch(any()) }
    }
}
