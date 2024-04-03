package com.github.wanderwise_inc.app.viewmodel

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat

/**
 * @brief check if the permissions for tracking the user's location have been granted
 *
 * @return `true` if the permissions have been granted else `false`
 */
fun Context.hasLocationPermission(): Boolean {
    val coarseLocationPermission = ContextCompat
        .checkSelfPermission(
            this,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

    val fineLocationPermission = ContextCompat
        .checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

    return coarseLocationPermission && fineLocationPermission
}