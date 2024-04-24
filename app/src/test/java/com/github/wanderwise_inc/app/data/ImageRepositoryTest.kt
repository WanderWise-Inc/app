package com.github.wanderwise_inc.app.data

import android.app.Application
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import app.cash.turbine.test
import com.google.firebase.FirebaseApp
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.junit.Assert.*
import org.junit.Rule
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.runner.RunWith
import org.mockito.MockitoAnnotations
import org.mockito.junit.MockitoJUnit
import org.mockito.junit.MockitoRule
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class ImageRepositoryTest {

    @get:Rule val mockitoRule : MockitoRule = MockitoJUnit.rule()

    @Mock private lateinit var application: Application

    @Mock private lateinit var context : Context

    @Mock private lateinit var imageReference : StorageReference

    @Mock private lateinit var activityLauncher : ActivityResultLauncher<Intent>

    private lateinit var imageRepositoryImpl: ImageRepositoryImpl

    private val testPathFile = "https://lh3.googleusercontent.com/a/ACg8ocKuSpafO1jmH65vDl5SZ35E9NwFv07kyiO7tL110QRdwzRnSz5X=s96-c"

    @Before
    fun setup() {
        imageRepositoryImpl = ImageRepositoryImpl(application, activityLauncher, imageReference)
    }
    @Test
    fun dummy() {
        assertEquals(5, (2+3))
    }

    /**
     * Test for setCurrentFile
     */
    @Test
    fun `set Current File To A Value`() {
        val newFile = Uri.parse(testPathFile)
        assertEquals(newFile, imageRepositoryImpl.setCurrentFile(newFile))
    }

    @Test
    fun `set Current File To null`() {
        val newFile : Uri? = null
        assertEquals(newFile, imageRepositoryImpl.setCurrentFile(newFile))
    }

    /**
     * Test for getBitMap
     */
    @Test
    fun `bitmap is null for a null Uri`() = runBlocking {
        val uri = null

        imageRepositoryImpl.getBitMap(uri).test {
            val emission = awaitItem()
            assertEquals(null, emission)

            awaitComplete()
        }
    }

    /**
     * Test for getBitMap
     */
    @Test
    fun `bitmap should not be null if Uri isn't null`() = runBlocking {
        val uri = Uri.parse(testPathFile)

        imageRepositoryImpl.getBitMap(uri).test {
            val emission = awaitItem()
            assertNotEquals(null, emission)

            awaitComplete()
        }
    }

}