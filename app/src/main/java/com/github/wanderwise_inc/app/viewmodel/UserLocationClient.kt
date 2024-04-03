package com.github.wanderwise_inc.app.viewmodel

import android.content.Context
import android.location.Location
import com.google.android.gms.location.FusedLocationProviderClient
import kotlinx.coroutines.flow.Flow

class UserLocationClient(
    private val context: Context,
    private val client: FusedLocationProviderClient
): LocationClient {

    override fun getLocationUpdates(interval: Long): Flow<Location> {
        TODO("Not yet implemented")
    }
}