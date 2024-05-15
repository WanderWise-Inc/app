package com.github.wanderwise_inc.app.data

import android.content.Intent
import androidx.activity.result.ActivityResultLauncher
import com.google.firebase.FirebaseApp
import io.mockk.MockKAnnotations
import io.mockk.Runs
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.just
import io.mockk.verify
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment

@RunWith(RobolectricTestRunner::class)
class GoogleSignInLauncherTest {

    @MockK
    private lateinit var activityResultLauncher: ActivityResultLauncher<Intent>

    private lateinit var googleSignInLauncher: GoogleSignInLauncher

    @Before
    fun setup() {
        MockKAnnotations.init(this)
        FirebaseApp.initializeApp(RuntimeEnvironment.getApplication())
        googleSignInLauncher = GoogleSignInLauncher(activityResultLauncher, listOf())
    }

    @Test
    fun `signIn should launch activityResultLauncher`() {
        every { activityResultLauncher.launch(any()) } just Runs

        googleSignInLauncher.signIn()

        verify { activityResultLauncher.launch(any()) }
    }
}
