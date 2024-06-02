package com.github.wanderwise_inc.app.viewmodel

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat

/**
 * Check if the app has location permissions.
 *
 * @return true if the app has location permissions, false otherwise.
 */
fun Context.hasLocationPermission(): Boolean {
  val coarseLocationPermission =
      ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) ==
          PackageManager.PERMISSION_GRANTED

  val fineLocationPermission =
      ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) ==
          PackageManager.PERMISSION_GRANTED

  return coarseLocationPermission && fineLocationPermission
}
