package com.github.wanderwise_inc.app.di

import android.content.Intent
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContract
import com.github.wanderwise_inc.app.MainActivity
import com.google.firebase.FirebaseApp
import io.mockk.MockKAnnotations
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.impl.annotations.MockK
import org.junit.After
import androidx.datastore.core.DataStore
import com.github.wanderwise_inc.app.proto.location.SavedItineraries
import com.github.wanderwise_inc.app.viewmodel.LocationClient
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import io.mockk.every
import io.mockk.mockk
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class AppModuleTest {

    @MockK
    private lateinit var mainActivity: MainActivity

    @MockK
    private lateinit var launcher: ActivityResultLauncher<Intent>

    private lateinit var appModule: AppModule

    @Before
    fun setUp() {
        MockKAnnotations.init(this, relaxed = true)

        every { mainActivity.registerForActivityResult(
            any<ActivityResultContract<Intent, ActivityResult>>(), any()
        ) } answers {
            launcher
        }

        FirebaseApp.initializeApp(RuntimeEnvironment.getApplication())

        appModule = AppModule(mainActivity)
    }

    @After
    fun tearDown() {
        clearAllMocks()
        FirebaseApp.getInstance().delete()
    }
  @Before
  fun setUp() {
    val mockCollectionRef = mockk<CollectionReference>()
    val mockStorageref = mockk<StorageReference>()
    val mockDb = mockk<FirebaseFirestore>()
    val mockStorage = mockk<FirebaseStorage>()
    every { mockDb.collection(any()) } returns mockCollectionRef
    every { mockStorage.getReference() } returns mockStorageref

    // FirebaseApp.initializeApp(RuntimeEnvironment.getApplication())
    AppModule.initialize(
        mockk<ActivityResultLauncher<Intent>>(),
        mockk<ActivityResultLauncher<Intent>>(),
        mockk<Intent>(),
        mockk<LocationClient>(),
        mockk<DataStore<SavedItineraries>>(),
        mockk<Context>(),
        mockk<FirebaseAuth>(),
        mockDb,
        mockStorage)
  }

    @Test
    fun `firebaseAuth should return only one instance of FirebaseAuth`() {
        val result1 = appModule.firebaseAuth
        assertNotNull(result1)

        val result2 = appModule.firebaseAuth
        assertEquals(result1, result2)
    }

    @Test
    fun `firebaseStorage should return only one instance of FirebaseStorage`() {
        val result1 = appModule.firebaseStorage
        assertNotNull(result1)

        val result2 = appModule.firebaseStorage
        assertEquals(result1, result2)
    }

    @Test
    fun `imageRepository should return only one instance of ImageRepository`() {
        val result1 = appModule.imageRepository
        assertNotNull(result1)

        val result2 = appModule.imageRepository
        assertEquals(result1, result2)
    }

    @Test
    fun `directionsRepository should return only one instance of DirectionsRepository`() {
        val result1 = appModule.directionsRepository
        assertNotNull(result1)

        val result2 = appModule.directionsRepository
        assertEquals(result1, result2)
    }

    @Test
    fun `itineraryRepository should return only one instance of ItineraryRepository`() {
        val result1 = appModule.itineraryRepository
        assertNotNull(result1)

        val result2 = appModule.itineraryRepository
        assertEquals(result1, result2)
    }

    @Test
    fun `profileRepository should return only one instance of ProfileRepository`() {
        val result1 = appModule.profileRepository
        assertNotNull(result1)

        val result2 = appModule.profileRepository
        assertEquals(result1, result2)
    }

    @Test
    fun `bottomNavigationViewModel should return only one instance of BottomNavigationViewModel`() {
        val result1 = appModule.bottomNavigationViewModel
        assertNotNull(result1)

        val result2 = appModule.bottomNavigationViewModel
        assertEquals(result1, result2)
    }

    @Test
    fun `createItineraryViewModel should return only one instance of CreateItineraryViewModel`() {
        val result1 = appModule.createItineraryViewModel
        assertNotNull(result1)

        val result2 = appModule.createItineraryViewModel
        assertEquals(result1, result2)
    }


    @Test
    fun `itineraryViewModel should return only one instance of ItineraryViewModel`() {
        val result1 = appModule.itineraryViewModel
        assertNotNull(result1)

        val result2 = appModule.itineraryViewModel
        assertEquals(result1, result2)
    }

    @Test
    fun `loginViewModel should return only one instance of LoginViewModel`() {
        val result1 = appModule.loginViewModel
        assertNotNull(result1)

        val result2 = appModule.loginViewModel
        assertEquals(result1, result2)
    }

    @Test
    fun `profileViewModel should return only one instance of ProfileViewModel`() {
        val result1 = appModule.profileViewModel
        assertNotNull(result1)

        val result2 = appModule.profileViewModel
        assertEquals(result1, result2)
    }
}
