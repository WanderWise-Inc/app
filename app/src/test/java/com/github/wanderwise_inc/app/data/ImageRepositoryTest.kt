package com.github.wanderwise_inc.app.data

import android.app.Application
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import app.cash.turbine.test
import coil.request.SuccessResult
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.TaskCompletionSource
import com.google.firebase.FirebaseApp
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import io.mockk.mockkStatic
import kotlinx.coroutines.CancellableContinuation
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.junit.Assert.*
import org.junit.Rule
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.runner.RunWith
import org.mockito.Mockito.any
import org.mockito.Mockito.anyString
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import org.mockito.invocation.InvocationOnMock
import org.mockito.junit.MockitoJUnit
import org.mockito.junit.MockitoRule
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.doAnswer
import org.mockito.kotlin.verify
import org.mockito.stubbing.Answer
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import kotlin.coroutines.resume

@RunWith(RobolectricTestRunner::class)
class ImageRepositoryTest {

    @get:Rule val mockitoRule : MockitoRule = MockitoJUnit.rule()

    @Mock private lateinit var imageReference : StorageReference
    @Mock private lateinit var storageReference: StorageReference
    @Mock private lateinit var onSuccessListener: OnSuccessListener<Void>
    @Mock private lateinit var onSuccessListenerArray : OnSuccessListener<in ByteArray>
    @Mock private lateinit var onFailureListener: OnFailureListener
    @Mock private lateinit var activityLauncher : ActivityResultLauncher<Intent>
    @Mock private lateinit var uploadTask: UploadTask
    @Mock private lateinit var taskByteArray : Task<ByteArray>
    @Mock private lateinit var cancellableContinuation: CancellableContinuation<ByteArray?>

    private lateinit var imageRepositoryImpl: ImageRepositoryImpl

    private val mockByteArray = byteArrayOf(1, 2, 3)

    private val testPathFile = "https://lh3.googleusercontent.com/a/ACg8ocKuSpafO1jmH65vDl5SZ35E9NwFv07kyiO7tL110QRdwzRnSz5X=s96-c"

    private val  uri = Uri.parse(testPathFile)

    @Before
    fun setup() {
        imageRepositoryImpl = ImageRepositoryImpl(activityLauncher, imageReference, uri)
    }

    /**
     * Test for setCurrentFile
     */
    @Test
    fun `set Current File To A Value`() {
        val newFile = Uri.parse(testPathFile)
        imageRepositoryImpl.setCurrentFile(newFile)
        assertEquals(newFile, imageRepositoryImpl.getCurrentFile())
    }

    @Test
    fun `set Current File To null`() {
        val newFile : Uri? = null
        imageRepositoryImpl.setCurrentFile(newFile)
        assertEquals(null, newFile)
    }

    @Test
    fun `upload image should correctly store the Uri in storage`() {
        val testPath = "testPath"

        `when`(imageReference.child(any(String::class.java))).thenReturn(storageReference)
        `when`(storageReference.putFile(any(Uri::class.java))).thenReturn(uploadTask)
        `when`(uploadTask.addOnSuccessListener(any())).thenAnswer {
            onSuccessListener.onSuccess(null)
            uploadTask
        }

        val success = imageRepositoryImpl.uploadImageToStorage(testPath)

        verify(imageReference).child("images/${testPath}")
        verify(storageReference).putFile(uri)
        assertEquals(true, success)
    }

    @Test
    fun `upload image when putFile failed should return false`() {
        val testPath = "testPath"

        `when`(imageReference.child(any(String::class.java))).thenReturn(storageReference)
        `when`(storageReference.putFile(any(Uri::class.java))).thenReturn(uploadTask)
        `when`(uploadTask.addOnFailureListener(any())).thenAnswer {
            onFailureListener.onFailure(Exception("putFile throws an exception"))
            uploadTask
        }

        val success = imageRepositoryImpl.uploadImageToStorage(testPath)

        verify(imageReference).child("images/${testPath}")
        verify(storageReference).putFile(uri)
        assertEquals(false, success)
    }

    @Test(expected=Exception::class)
    fun `if path is empty, uploadImageToStorage should throw an exception`() {
        val testPath = ""
        imageRepositoryImpl.uploadImageToStorage(testPath)
    }

    @Test
    fun `if file is null, uploadImageToStorage should return false`() {
        val testPath = "random"
        imageRepositoryImpl.setCurrentFile(null)
        assertEquals(false, imageRepositoryImpl.uploadImageToStorage(testPath))
    }

    @Test(expected=Exception::class)
    fun `if putFile throws an exception, the method should return false`() {
        val testPath = "random"

        `when`(imageReference.child(any(String::class.java))).thenReturn(storageReference)
        `when`(storageReference.putFile(any(Uri::class.java))).thenThrow(Exception("putFile throws an exception"))
        val success = imageRepositoryImpl.uploadImageToStorage(testPath)
        assertEquals(false, success)
    }

    @Test
    fun `fetch image with an empty string should return null`() = runTest {
        val bitMap = imageRepositoryImpl.fetchImage("").first()
        assertEquals(null, bitMap)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `fetch image with a correct path should correctly return a bitMap`() = runTest {
        val byteArray = byteArrayOf(1, 2, 3)
        `when`(imageReference.child(any(String::class.java))).thenReturn(storageReference)
        `when`(storageReference.getBytes(any(Long::class.java))).thenReturn(taskByteArray)
        `when`(taskByteArray.addOnSuccessListener(any())).thenAnswer {
            onSuccessListenerArray.onSuccess(byteArray)
            cancellableContinuation.resume(byteArray)
            taskByteArray
        }
        val bitMap = imageRepositoryImpl.fetchImage("testPath").first()
        assertNotNull(bitMap)
    }
}