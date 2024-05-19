package com.github.wanderwise_inc.app.data

import android.content.Intent
import android.net.Uri
import androidx.activity.result.ActivityResultLauncher
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.any
import org.mockito.Mockito.anyString
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnit
import org.mockito.junit.MockitoRule
import org.mockito.kotlin.verify
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class ImageRepositoryTest {

  @get:Rule val mockitoRule: MockitoRule = MockitoJUnit.rule()

  @Mock private lateinit var imageReference: StorageReference
  @Mock private lateinit var storageReference: StorageReference
  @Mock private lateinit var activityLauncher: ActivityResultLauncher<Intent>
  @Mock private lateinit var uploadTask: UploadTask
  @Mock private lateinit var taskUri: Task<Uri>

  private lateinit var imageRepositoryImpl: ImageRepositoryImpl

  private val testPathFile =
      "https://lh3.googleusercontent.com/a/ACg8ocKuSpafO1jmH65vDl5SZ35E9NwFv07kyiO7tL110QRdwzRnSz5X=s96-c"

  private val uri = Uri.parse(testPathFile)

  @Before
  fun setup() {
    imageRepositoryImpl = ImageRepositoryImpl(activityLauncher, imageReference, uri)
  }

  /** Test for setCurrentFile */
  @Test
  fun `set Current File To A Value`() {
    val newFile = Uri.parse(testPathFile)
    imageRepositoryImpl.setCurrentFile(newFile)
    assertEquals(newFile, imageRepositoryImpl.getCurrentFile())
  }

  @Test
  fun `set Current File To null`() {
    val newFile: Uri? = null
    imageRepositoryImpl.setCurrentFile(newFile)
    assertEquals(null, newFile)
  }

  @Test
  fun `get current file should return null if the currentFile is null`() {
    imageRepositoryImpl.setCurrentFile(null)
    assertNull(imageRepositoryImpl.getCurrentFile())
  }

  @Test
  fun `get current file should return the currentFile if it isn't null`() {
    imageRepositoryImpl.setCurrentFile(uri)
    assertNotNull(imageRepositoryImpl.getCurrentFile())
  }

  @Test
  fun `launch activity should called the launch function with the intent`() {
    val it = Intent()
    imageRepositoryImpl.launchActivity(it)
    verify(activityLauncher).launch(it)
  }

  @Test
  fun `upload image should correctly store the Uri in storage`() = runTest {
    val testPath = "testPath"

    `when`(imageReference.child(anyString())).thenReturn(storageReference)
    `when`(storageReference.putFile(any(Uri::class.java))).thenReturn(uploadTask)
    `when`(uploadTask.addOnSuccessListener(any())).thenAnswer {
      val listener = it.arguments[0] as OnSuccessListener<Void>
      listener.onSuccess(null)
      uploadTask
    }
    `when`(uploadTask.addOnFailureListener(any())).thenReturn(uploadTask)

    val success = imageRepositoryImpl.uploadImageToStorage(testPath)

    verify(imageReference).child("images/${testPath}")
    verify(storageReference).putFile(uri)
    assertEquals(true, success)
  }

  @Test
  fun `upload image when putFile failed should return false`() = runTest {
    val testPath = "testPath"

    `when`(imageReference.child(anyString())).thenReturn(storageReference)
    `when`(storageReference.putFile(uri)).thenReturn(uploadTask)
    `when`(uploadTask.addOnSuccessListener(any())).thenReturn(uploadTask)
    `when`(uploadTask.addOnFailureListener(any())).thenAnswer {
      val listener = it.arguments[0] as OnFailureListener
      listener.onFailure(Exception("putFile failed"))
      uploadTask
    }

    val success = imageRepositoryImpl.uploadImageToStorage(testPath)

    verify(imageReference).child("images/${testPath}")
    verify(storageReference).putFile(uri)
    assertEquals(false, success)
  }

  @Test(expected = Exception::class)
  fun `if path is empty, uploadImageToStorage should throw an exception`() = runTest {
    val testPath = ""
    imageRepositoryImpl.uploadImageToStorage(testPath)
  }

  @Test
  fun `if file is null, uploadImageToStorage should return false`() = runTest {
    val testPath = "random"
    imageRepositoryImpl.setCurrentFile(null)
    assertEquals(false, imageRepositoryImpl.uploadImageToStorage(testPath))
  }

  @Test(expected = Exception::class)
  fun `if putFile throws an exception, the method should return false`() = runTest {
    val testPath = "random"

    `when`(imageReference.child(anyString())).thenReturn(storageReference)
    `when`(storageReference.putFile(any(Uri::class.java)))
        .thenThrow(Exception("putFile throws an exception"))
    val success = imageRepositoryImpl.uploadImageToStorage(testPath)
    assertEquals(false, success)
  }

  @Test
  fun `fetch image with an empty string should return null`() = runTest {
    val bitMap = imageRepositoryImpl.fetchImage("").first()
    assertEquals(null, bitMap)
  }

  @Test
  fun `fetch image with a correct path should correctly return a Uri`() = runTest {
    `when`(imageReference.child(anyString())).thenReturn(storageReference)
    `when`(storageReference.downloadUrl).thenReturn(taskUri)
    `when`(taskUri.addOnSuccessListener(any())).thenAnswer {
      val listener = it.arguments[0] as OnSuccessListener<Uri>
      listener.onSuccess(uri)
      taskUri
    }

    val uriRes = imageRepositoryImpl.fetchImage("testPath").first()
    assertEquals(uri, uriRes)
  }

  @Test
  fun `fetch image with an error while getting the download URL should return a null Uri`() =
      runTest {
        `when`(imageReference.child(anyString())).thenReturn(storageReference)
        `when`(storageReference.downloadUrl).thenReturn(taskUri)
        `when`(taskUri.addOnSuccessListener(any())).thenReturn(taskUri)
        `when`(taskUri.addOnFailureListener(any())).thenAnswer {
          val listener = it.arguments[0] as OnFailureListener
          listener.onFailure(Exception("Get bytes return an exception"))
          taskUri
        }

        val uriRes = imageRepositoryImpl.fetchImage("testPath").first()
        assertNull(uriRes)
      }

  @Test
  fun `fetch image with downloadURL that returns null should return a null Uri`() = runTest {
    `when`(imageReference.child(anyString())).thenReturn(storageReference)
    `when`(storageReference.downloadUrl).thenReturn(taskUri)
    `when`(taskUri.addOnSuccessListener(any())).thenAnswer {
      val listener = it.arguments[0] as OnSuccessListener<Uri>
      listener.onSuccess(null)
      taskUri
    }

    val uriRes = imageRepositoryImpl.fetchImage("testPath").first()
    assertNull(uriRes)
  }
}
