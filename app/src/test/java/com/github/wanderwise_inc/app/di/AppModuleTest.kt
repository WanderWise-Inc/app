package com.github.wanderwise_inc.app.di

import android.content.Context
import android.content.Intent
import androidx.activity.result.ActivityResultLauncher
import com.firebase.ui.auth.AuthUI
import com.github.wanderwise_inc.app.data.DirectionsRepository
import com.github.wanderwise_inc.app.data.ImageRepository
import com.github.wanderwise_inc.app.data.ItineraryRepository
import com.github.wanderwise_inc.app.data.ProfileRepository
import com.google.firebase.FirebaseApp
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import io.mockk.every
import io.mockk.mockk
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment

@RunWith(RobolectricTestRunner::class)
class AppModuleTest {

    @Before
    fun setUp() {
        FirebaseApp.initializeApp(RuntimeEnvironment.getApplication())
    }

    @Test
    fun `provideFirebaseAuth should return an instance of FirebaseAuth`() {
        val result = AppModule.provideFirebaseAuth()

        assertNotNull(result)
    }

    @Test
    fun `provideFirebaseStorage should return an instance of FirebaseStorage`() {
        val result = AppModule.provideFirebaseStorage()

        assertNotNull(result)
    }

    @Test
    fun `provideImageRepository should return an instance of ImageRepository`() {
        val imageLauncher = mockk<ActivityResultLauncher<Intent>>()
        val firebaseStorage = mockk<FirebaseStorage>()

        every { firebaseStorage.getReference() } returns mockk<StorageReference>()

        val result = AppModule.provideImageRepository(imageLauncher, firebaseStorage)

        assertNotNull(result)
    }

    @Test
    fun `provideDirectionsRepository should return an instance of DirectionsRepository`() {
        val result = AppModule.provideDirectionsRepository()

        assertNotNull(result)
    }

    @Test
    fun `provideItineraryRepository should return an instance of ItineraryRepository`() {
        val result = AppModule.provideItineraryRepository()

        assertNotNull(result)
    }

    @Test
    fun `provideProfileRepository should return an instance of ProfileRepository`() {
        val result = AppModule.provideProfileRepository()

        assertNotNull(result)
    }

    @Test
    fun `provideProfileSignIn should return an instance of ProfileSignIn`() {
        val result = AppModule.provideSignInRepository()

        assertNotNull(result)
    }

    @Test
    fun `provideBottomNavigationViewModel should return an instance of BottomNavigationViewModel`() {
        val result = AppModule.provideBottomNavigationViewModel()

        assertNotNull(result)
    }

    @Test
    fun `provideMapViewModel should return an instance of MapViewModel`() {
        val context = mockk<Context>(relaxed = true)
        val itineraryRepository = mockk<ItineraryRepository>()
        val directionsRepository = mockk<DirectionsRepository>()

        val result = AppModule.provideMapViewModel(context, itineraryRepository, directionsRepository)

        assertNotNull(result)
    }

    @Test
    fun `provideProfileViewModel should return an instance of ProfileViewModel`() {
        val profileRepository = mockk<ProfileRepository>()
        val imageRepository = mockk<ImageRepository>()

        val result = AppModule.provideProfileViewModel(profileRepository, imageRepository)

        assertNotNull(result)
    }

    @Test
    fun `provideGoogleSignInLauncher should return an instance of GoogleSignInLauncher`() {
        val signInLauncher = mockk<ActivityResultLauncher<Intent>>()
        val providers = emptyList<AuthUI.IdpConfig>()

        val result = AppModule.provideGoogleSignInLauncher(signInLauncher, providers)

        assertNotNull(result)
    }
}
