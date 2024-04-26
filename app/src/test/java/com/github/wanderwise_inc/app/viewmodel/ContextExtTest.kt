package com.github.wanderwise_inc.app.viewmodel

import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import org.junit.Assert.*
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`

class ContextExtTest {

  @Test
  fun `test hasLocationPermission returns true when both permissions are granted`() {
    val context = mock(Context::class.java)

    // Grant both permission
    `when`(
            ContextCompat.checkSelfPermission(
                context, android.Manifest.permission.ACCESS_COARSE_LOCATION))
        .thenReturn(PackageManager.PERMISSION_GRANTED)

    `when`(
            ContextCompat.checkSelfPermission(
                context, android.Manifest.permission.ACCESS_FINE_LOCATION))
        .thenReturn(PackageManager.PERMISSION_GRANTED)

    assertTrue(context.hasLocationPermission())
  }

  @Test
  fun `test hasLocationPermission returns false when only one permission is granted`() {
    val context = mock(Context::class.java)

    // Grant only coarse location permission
    `when`(
            ContextCompat.checkSelfPermission(
                context, android.Manifest.permission.ACCESS_COARSE_LOCATION))
        .thenReturn(PackageManager.PERMISSION_GRANTED)

    `when`(
            ContextCompat.checkSelfPermission(
                context, android.Manifest.permission.ACCESS_FINE_LOCATION))
        .thenReturn(PackageManager.PERMISSION_DENIED)

    assertFalse(context.hasLocationPermission())

    // Grant only fine location permission
    `when`(
            ContextCompat.checkSelfPermission(
                context, android.Manifest.permission.ACCESS_COARSE_LOCATION))
        .thenReturn(PackageManager.PERMISSION_DENIED)

    `when`(
            ContextCompat.checkSelfPermission(
                context, android.Manifest.permission.ACCESS_FINE_LOCATION))
        .thenReturn(PackageManager.PERMISSION_GRANTED)

    assertFalse(context.hasLocationPermission())
  }

  @Test
  fun `test hasLocationPermission returns false when neither permission is granted`() {
    val context = mock(Context::class.java)

    // Grant no permission
    `when`(
            ContextCompat.checkSelfPermission(
                context, android.Manifest.permission.ACCESS_COARSE_LOCATION))
        .thenReturn(PackageManager.PERMISSION_DENIED)

    `when`(
            ContextCompat.checkSelfPermission(
                context, android.Manifest.permission.ACCESS_FINE_LOCATION))
        .thenReturn(PackageManager.PERMISSION_DENIED)

    assertFalse(context.hasLocationPermission())
  }
}
