package com.github.wanderwise_inc.app.ui.map

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.wanderwise_inc.app.model.location.Itinerary
import com.github.wanderwise_inc.app.model.location.ItineraryTags
import com.github.wanderwise_inc.app.model.location.PlacesReader
import com.github.wanderwise_inc.app.ui.map.PreviewItineraryScreen
import com.github.wanderwise_inc.app.viewmodel.MapViewModel
import kotlinx.coroutines.flow.flow
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnit
import org.mockito.junit.MockitoRule

@RunWith(AndroidJUnit4::class)
class PreviewItineraryScreenKtTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @get:Rule
    val mockitoRule: MockitoRule = MockitoJUnit.rule()

    @Mock
    private lateinit var mapViewModel: MapViewModel

    private val epflLat = 46.519126741544575
    private val epflLon = 6.5676006970802145

    @Before
    fun setUp() {
        val epflLocation = Mockito.mock(android.location.Location::class.java)
        epflLocation.latitude = epflLat
        epflLocation.longitude = epflLon

        val locations = PlacesReader(null).readFromString()
        val itinerary = Itinerary(
            userUid = "",
            locations = locations,
            title = "San Francisco Bike Itinerary",
            tags = listOf(ItineraryTags.CULTURAL, ItineraryTags.NATURE, ItineraryTags.BUDGET),
            description = "A 3-day itinerary to explore the best of San Francisco on a bike.",
            visible = true
        )

        `when`(mapViewModel.getUserLocation()).thenReturn(flow { emit(epflLocation) })

        composeTestRule.setContent {
            PreviewItineraryScreen(itinerary, mapViewModel)
        }
    }

    @Test
    fun previewItineraryScreen() {
        composeTestRule.onNodeWithTag("Map screen").assertIsDisplayed()
        composeTestRule.onNodeWithTag("Google Maps").assertIsDisplayed()
        composeTestRule.onNodeWithTag("Center Button").assertIsDisplayed()
    }

    @Test
    fun centerButton() {
        composeTestRule.onNodeWithTag("Center Button").assertIsDisplayed()

        composeTestRule.onNodeWithTag("Center Button").performClick()


    }
}
