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
import org.mockito.MockitoAnnotations
import org.mockito.junit.MockitoJUnit
import org.mockito.junit.MockitoRule

class ImageRepositoryTest {

    @get:Rule val mockitoRule : MockitoRule = MockitoJUnit.rule()

    @Mock private lateinit var application: Application

    @Mock private lateinit var context : Context

    @Mock private lateinit var imageReference : StorageReference

    @Mock private lateinit var activityLauncher : ActivityResultLauncher<Intent>

    private lateinit var imageRepositoryImpl: ImageRepositoryImpl

    private val testPathFile = "https://firebasestorage.googleapis.com/v0/b/wanderwise-d8d36.appspot.com/o/images%2FprofilePicture%2FdefaultProfilePicture.jpg?alt=media&token=4137ad42-8b48-43f2-b6bf-8d814afbad01"

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
        val uri = Uri.parse(testPathFile)

        imageRepositoryImpl.getBitMap(uri).test {
            val emission = awaitItem()
            assertEquals(null, emission)

            awaitComplete()
        }
    }

}