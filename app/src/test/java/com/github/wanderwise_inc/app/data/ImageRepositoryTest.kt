package com.github.wanderwise_inc.app.data

import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.activity.result.ActivityResultLauncher
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import kotlinx.coroutines.CancellableContinuation
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
  @Mock private lateinit var onSuccessListener: OnSuccessListener<Void>
  @Mock private lateinit var onSuccessListenerArray: OnSuccessListener<in ByteArray>
  @Mock private lateinit var onFailureListener: OnFailureListener
  @Mock private lateinit var activityLauncher: ActivityResultLauncher<Intent>
  @Mock private lateinit var uploadTask: UploadTask
  @Mock private lateinit var taskByteArray: Task<ByteArray>
  @Mock private lateinit var cancellableContinuation: CancellableContinuation<ByteArray?>

  private lateinit var imageRepositoryImpl: ImageRepositoryImpl

  private val mockByteArray = byteArrayOf(1, 2, 3)

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

  @Test(expected = Exception::class)
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

  @Test(expected = Exception::class)
  fun `if putFile throws an exception, the method should return false`() {
    val testPath = "random"

    `when`(imageReference.child(any(String::class.java))).thenReturn(storageReference)
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
  fun `fetch image with a correct path should correctly return a bitMap`() = runTest {
    val byteArray = byteArrayOf(1, 2, 3)
    `when`(imageReference.child(any(String::class.java))).thenReturn(storageReference)
    `when`(storageReference.getBytes(any(Long::class.java))).thenReturn(taskByteArray)
    `when`(taskByteArray.addOnSuccessListener(any())).thenAnswer {
      val listener = it.arguments[0] as OnSuccessListener<ByteArray>
      listener.onSuccess(byteArray)
      taskByteArray
    }

    val byteR = imageRepositoryImpl.fetchImage("testPath").first()
    val bitMap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
    assertNotNull(byteR)
    assertEquals(bitMap.width, byteR!!.width)
    assertEquals(bitMap.height, byteR.height)
    for (x in 0 until bitMap.width) {
      for (y in 0 until bitMap.height) {
        assertEquals(bitMap.getPixel(x, y), byteR.getPixel(x, y))
      }
    }
  }

  @Test
  fun `fetch image with an error for while getting the bytes should return a null bitmap`() =
      runTest {
        `when`(imageReference.child(any(String::class.java))).thenReturn(storageReference)
        `when`(storageReference.getBytes(any(Long::class.java))).thenReturn(taskByteArray)
        `when`(taskByteArray.addOnFailureListener(any())).thenAnswer {
          val listener = it.arguments[0] as OnFailureListener
          listener.onFailure(Exception("Get bytes return an exception"))
          taskByteArray
        }

        val bitmap = imageRepositoryImpl.fetchImage("testPath").first()
        assertNull(bitmap)
      }

  @Test
  fun `fetch image with getBytes that returns null should return a null bitmap`() = runTest {
    `when`(imageReference.child(any(String::class.java))).thenReturn(storageReference)
    `when`(storageReference.getBytes(any(Long::class.java))).thenReturn(taskByteArray)
    `when`(taskByteArray.addOnSuccessListener(any())).thenAnswer {
      val listener = it.arguments[0] as OnSuccessListener<ByteArray>
      listener.onSuccess(null)
      taskByteArray
    }

    val bitmap = imageRepositoryImpl.fetchImage("testPath").first()
    assertNull(bitmap)
  }
}
