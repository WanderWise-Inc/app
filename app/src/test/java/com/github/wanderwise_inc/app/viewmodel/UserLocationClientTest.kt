package com.github.wanderwise_inc.app.viewmodel

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.LocationManager
import com.github.wanderwise_inc.app.model.location.Location
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationResult
import com.google.android.gms.maps.model.LatLng
import io.mockk.MockKAnnotations
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.jupiter.api.assertThrows

class UserLocationClientTest {

  @MockK private lateinit var context: Context

  @MockK private lateinit var locationManager: LocationManager

  @MockK private lateinit var client: FusedLocationProviderClient

  private lateinit var locationClient: LocationClient

  private val epflLat = 46.519126741544575
  private val epflLon = 6.5676006970802145

  @Before
  fun setup() {
    MockKAnnotations.init(this)
    locationClient = UserLocationClient(context, client)
  }

  @After
  fun tearDown() {
    clearAllMocks()
  }

  @Test
  fun `getLocationUpdates returns correct location`() = runBlocking {
    // Mock necessary dependencies and behavior
    val location = Location.placeLocation(LatLng(epflLat, epflLon))

    every { context.checkPermission(any(), any(), any()) } returns PackageManager.PERMISSION_GRANTED

    every { context.getSystemService(any()) } returns locationManager

    every { locationManager.isProviderEnabled(any()) } returns true

    every { client.requestLocationUpdates(any(), any<LocationCallback>(), any()) } answers
        {
          val callback = invocation.args[1] as LocationCallback
          val androidLocation = mockk<android.location.Location>()
          every { androidLocation.latitude } returns epflLat
          every { androidLocation.longitude } returns epflLon
          callback.onLocationResult(LocationResult.create(listOf(androidLocation)))
          mockk()
        }

    every { client.removeLocationUpdates(any<LocationCallback>()) } returns mockk()

    // Call the method under test
    val flow = locationClient.getLocationUpdates(1000)
    val emittedLocation = flow.first()

    // Verify behavior
    verify { client.requestLocationUpdates(any(), any<LocationCallback>(), any()) }

    // Check the emitted location
    assertEquals(location, emittedLocation)
  }

  @Test
  fun `getLocationUpdates throws correct error when interval is invalid`() {
    assertThrows<IllegalArgumentException> { locationClient.getLocationUpdates(0) }

    assertThrows<IllegalArgumentException> { locationClient.getLocationUpdates(-1) }
  }

  @Test
  fun `getLocationUpdates throws correct error when the permissions are not granted`() =
      runBlocking<Unit> {
        // Mock necessary dependencies and behavior
        every {
          context.checkPermission(Manifest.permission.ACCESS_COARSE_LOCATION, any(), any())
        } returns PackageManager.PERMISSION_DENIED

        every {
          context.checkPermission(Manifest.permission.ACCESS_FINE_LOCATION, any(), any())
        } returns PackageManager.PERMISSION_DENIED

        val flow = locationClient.getLocationUpdates(1000)

        assertThrows<LocationClient.LocationException> { flow.first() }
      }

  @Test
  fun `getLocationUpdates throws correct error when no mean of tracking is available`() =
      runBlocking<Unit> {
        every { context.checkPermission(any(), any(), any()) } returns
            PackageManager.PERMISSION_GRANTED

        every { context.getSystemService(Context.LOCATION_SERVICE) } returns locationManager

        every { locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) } returns false

        every { locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER) } returns false

        assertThrows<LocationClient.LocationException> {
          locationClient.getLocationUpdates(1000).first()
        }
      }
}
