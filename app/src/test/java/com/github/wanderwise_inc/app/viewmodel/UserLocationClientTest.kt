package com.github.wanderwise_inc.app.viewmodel

import android.content.Context
import android.location.Location
import android.location.LocationManager
import android.os.Looper
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.ArgumentMatchers.any
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.junit.MockitoJUnit
import org.mockito.junit.MockitoRule

class UserLocationClientTest {

    @get:Rule
    val mockitoRule: MockitoRule = MockitoJUnit.rule()

    @Mock
    private lateinit var context: Context

    @Mock
    private lateinit var locationManager: LocationManager

    @Mock
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    private lateinit var userLocationClient: UserLocationClient

    private val epflLat = 46.519126741544575
    private val epflLon = 6.5676006970802145

    @Before
    fun setup() {
        userLocationClient = UserLocationClient(context, fusedLocationProviderClient)
    }

    @Test
    fun `getLocationUpdates returns correct location`() = runBlocking {
        // Mock necessary dependencies and behavior
        val location = Location(null)
        location.latitude = epflLat
        location.longitude = epflLon

        `when`(context.hasLocationPermission()).thenReturn(true)
        `when`(context.getSystemService(Context.LOCATION_SERVICE)).thenReturn(locationManager)

        `when`(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)).thenReturn(true)
        `when`(locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)).thenReturn(true)

        `when`(fusedLocationProviderClient.requestLocationUpdates(
            any(LocationRequest::class.java),
            any(LocationCallback::class.java),
            any(Looper::class.java))
        ).thenAnswer {
            val callback = it.arguments[1] as LocationCallback
            callback.onLocationResult(LocationResult.create(listOf(location)))
        }

        // Call the method under test
        val flow = userLocationClient.getLocationUpdates(1000)

        // Verify behavior
        verify(fusedLocationProviderClient).requestLocationUpdates(
            any(LocationRequest::class.java),
            any(LocationCallback::class.java),
            any(Looper::class.java)
        )

        // Check the emitted location
        val emittedLocation = flow.first()
        assertEquals(location, emittedLocation)
    }

    // Add more tests as needed
}
