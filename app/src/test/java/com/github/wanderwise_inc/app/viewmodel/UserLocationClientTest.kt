package com.github.wanderwise_inc.app.viewmodel

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.tasks.Task
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.ArgumentMatchers.any
import org.mockito.ArgumentMatchers.anyInt
import org.mockito.ArgumentMatchers.eq
import org.mockito.Mock
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import org.mockito.Mockito.verify
import org.mockito.junit.MockitoJUnit
import org.mockito.junit.MockitoRule
import kotlin.IllegalArgumentException

class UserLocationClientTest {

    @get:Rule
    val mockitoRule: MockitoRule = MockitoJUnit.rule()

    @Mock
    private lateinit var context: Context

    @Mock
    private lateinit var locationManager: LocationManager

    @Mock
    private lateinit var client: FusedLocationProviderClient

    private lateinit var userLocationClient: UserLocationClient

    private val epflLat = 46.519126741544575
    private val epflLon = 6.5676006970802145

    @Before
    fun setup() {
        userLocationClient = UserLocationClient(context, client)
    }

    @Test
    fun `getLocationUpdates returns correct location`() = runBlocking {
        // Mock necessary dependencies and behavior
        val location = mock(Location::class.java)
        location.latitude = epflLat
        location.longitude = epflLon

        `when`(context.checkPermission(
            eq(Manifest.permission.ACCESS_COARSE_LOCATION),
            anyInt(),
            anyInt()
        )).thenReturn(PackageManager.PERMISSION_GRANTED)
        `when`(context.checkPermission(
            eq(Manifest.permission.ACCESS_FINE_LOCATION),
            anyInt(),
            anyInt()
        )).thenReturn(PackageManager.PERMISSION_GRANTED)
        `when`(context.getSystemService(Context.LOCATION_SERVICE)).thenReturn(locationManager)

        `when`(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)).thenReturn(true)
        `when`(locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)).thenReturn(true)

        `when`(client.requestLocationUpdates(
            any(LocationRequest::class.java),
            any(LocationCallback::class.java),
            any()
        )).thenAnswer {
            val callback = it.arguments[1] as LocationCallback
            callback.onLocationResult(LocationResult.create(listOf(location)))
            mock(Task::class.java) as Task<*>
        }

        // Call the method under test
        val flow = userLocationClient.getLocationUpdates(1000)
        val emittedLocation = flow.first()

        // Verify behavior
        verify(client).requestLocationUpdates(
            any(LocationRequest::class.java),
            any(LocationCallback::class.java),
            any()
        )

        // Check the emitted location
        assertEquals(location, emittedLocation)
    }

    @Test
    fun `getLocationUpdates throws correct error when interval is invalid`() {
        assertThrows<IllegalArgumentException> {
            userLocationClient.getLocationUpdates(0)
        }

        assertThrows<IllegalArgumentException> {
            userLocationClient.getLocationUpdates(-1)
        }
    }

    @Test
    fun `getLocationUpdates throws correct error when the permissions are not granted`() = runBlocking<Unit> {
        // Mock necessary dependencies and behavior
        `when`(context.checkPermission(
            eq(Manifest.permission.ACCESS_COARSE_LOCATION),
            anyInt(),
            anyInt()
        )).thenReturn(PackageManager.PERMISSION_DENIED)
        `when`(context.checkPermission(
            eq(Manifest.permission.ACCESS_FINE_LOCATION),
            anyInt(),
            anyInt()
        )).thenReturn(PackageManager.PERMISSION_DENIED)

        val flow = userLocationClient.getLocationUpdates(1000)

        assertThrows<LocationClient.LocationException> {
            flow.first()
        }
    }

    @Test
    fun `getLocationUpdates throws correct error when no mean of tracking is available`() = runBlocking<Unit> {
        `when`(context.checkPermission(
            eq(Manifest.permission.ACCESS_COARSE_LOCATION),
            anyInt(),
            anyInt()
        )).thenReturn(PackageManager.PERMISSION_GRANTED)
        `when`(context.checkPermission(
            eq(Manifest.permission.ACCESS_FINE_LOCATION),
            anyInt(),
            anyInt()
        )).thenReturn(PackageManager.PERMISSION_GRANTED)
        `when`(context.getSystemService(Context.LOCATION_SERVICE)).thenReturn(locationManager)

        `when`(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)).thenReturn(false)
        `when`(locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)).thenReturn(false)

        assertThrows<LocationClient.LocationException> {
            userLocationClient.getLocationUpdates(1000).first()
        }
    }
}
