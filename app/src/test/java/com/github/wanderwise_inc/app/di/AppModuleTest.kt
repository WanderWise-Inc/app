package com.github.wanderwise_inc.app.di

import android.content.Context
import android.content.Intent
import androidx.activity.result.ActivityResultLauncher
import androidx.datastore.core.DataStore
import com.github.wanderwise_inc.app.proto.location.SavedItineraries
import com.github.wanderwise_inc.app.viewmodel.LocationClient
import com.google.firebase.FirebaseApp
import io.mockk.mockk
import org.junit.Assert.assertEquals
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
    AppModule.initialize(
        mockk<ActivityResultLauncher<Intent>>(),
        mockk<ActivityResultLauncher<Intent>>(),
        mockk<Intent>(),
        mockk<LocationClient>(),
        mockk<DataStore<SavedItineraries>>(),
        mockk<Context>())
  }

  @Test
  fun `firebaseAuth should return only one instance of FirebaseAuth`() {
    val result1 = AppModule.firebaseAuth
    assertNotNull(result1)

    val result2 = AppModule.firebaseAuth
    assertEquals(result1, result2)
  }

  @Test
  fun `firebaseStorage should return only one instance of FirebaseStorage`() {
    val result1 = AppModule.firebaseStorage
    assertNotNull(result1)

    val result2 = AppModule.firebaseStorage
    assertEquals(result1, result2)
  }

  @Test
  fun `imageRepository should return only one instance of ImageRepository`() {
    val result1 = AppModule.imageRepository
    assertNotNull(result1)

    val result2 = AppModule.imageRepository
    assertEquals(result1, result2)
  }

  @Test
  fun `directionsRepository should return only one instance of DirectionsRepository`() {
    val result1 = AppModule.directionsRepository
    assertNotNull(result1)

    val result2 = AppModule.directionsRepository
    assertEquals(result1, result2)
  }

  @Test
  fun `locationsRepository should return only one instance of LocationsRepository`() {
    val result1 = AppModule.locationsRepository
    assertNotNull(result1)

    val result2 = AppModule.locationsRepository
    assertEquals(result1, result2)
  }

  @Test
  fun `itineraryRepository should return only one instance of ItineraryRepository`() {
    val result1 = AppModule.itineraryRepository
    assertNotNull(result1)

    val result2 = AppModule.itineraryRepository
    assertEquals(result1, result2)
  }

  @Test
  fun `profileRepository should return only one instance of ProfileRepository`() {
    val result1 = AppModule.profileRepository
    assertNotNull(result1)

    val result2 = AppModule.profileRepository
    assertEquals(result1, result2)
  }

  @Test
  fun `signInRepository should return only one instance of ProfileSignIn`() {
    val result1 = AppModule.signInRepository
    assertNotNull(result1)

    val result2 = AppModule.signInRepository
    assertEquals(result1, result2)
  }

  @Test
  fun `bottomNavigationViewModel should return only one instance of BottomNavigationViewModel`() {
    val result1 = AppModule.bottomNavigationViewModel
    assertNotNull(result1)

    val result2 = AppModule.bottomNavigationViewModel
    assertEquals(result1, result2)
  }

  @Test
  fun `createItineraryViewModel should return only one instance of MapViewModel`() {
    val result1 = AppModule.createItineraryViewModel
    assertNotNull(result1)

    val result2 = AppModule.createItineraryViewModel
    assertEquals(result1, result2)
  }

  @Test
  fun `itineraryViewModel should return only one instance of MapViewModel`() {
    val result1 = AppModule.itineraryViewModel
    assertNotNull(result1)

    val result2 = AppModule.itineraryViewModel
    assertEquals(result1, result2)
  }

  @Test
  fun `profileViewModel should return only one instance of ProfileViewModel`() {
    val result1 = AppModule.profileViewModel
    assertNotNull(result1)

    val result2 = AppModule.profileViewModel
    assertEquals(result1, result2)
  }

  @Test
  fun `googleSignInLauncher should return only one instance of GoogleSignInLauncher`() {
    val result1 = AppModule.googleSignInLauncher
    assertNotNull(result1)

    val result2 = AppModule.googleSignInLauncher
    assertEquals(result1, result2)
  }
}
