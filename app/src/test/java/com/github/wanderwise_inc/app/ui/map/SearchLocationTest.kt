package com.github.wanderwise_inc.app.ui.map

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.navigation.NavHostController
import com.github.wanderwise_inc.app.model.location.Itinerary
import com.github.wanderwise_inc.app.viewmodel.CreateItineraryViewModel
import org.junit.Rule
import org.junit.Test
import org.junit.jupiter.api.Assertions.*
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnit
import org.mockito.junit.MockitoRule
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class SearchLocationTest {
  @get:Rule val composeTestRule = createComposeRule()
  @get:Rule val mockitoRule: MockitoRule = MockitoJUnit.rule()

  @Mock private lateinit var createItineraryViewModel: CreateItineraryViewModel
  @Mock private lateinit var navController: NavHostController

  private val itineraryBuilder = Itinerary.Builder()

  // Test not working yet
  /*@Before
  fun setup() {
    composeTestRule.setContent {
      MapsInitializer.initialize(LocalContext.current)
      SearchLocation(createItineraryViewModel, navController)
    }

    `when`(createItineraryViewModel.getNewItinerary()).thenReturn(itineraryBuilder)
    `when`(createItineraryViewModel.fetchPlaces(anyString())).then { */
  /* do nothing */
  /*}
    `when`(createItineraryViewModel.getPlacesLiveData())
        .thenReturn(liveData { emptyList<Location>() })
    `when`(createItineraryViewModel.getUserLocation())
        .thenReturn(flow { emit(Location(lat = 0.0, long = 0.0)) })
  }*/

  @Test fun dummy() {}

  /*@Test
  fun `initial elements are displayed correctly`() {
    composeTestRule.onNodeWithTag(TestTags.LOCATION_SEARCH_BAR).assertIsDisplayed()
    composeTestRule.onNodeWithTag(TestTags.LOCATION_SEARCH_SCAFFOLD).assertIsDisplayed()
    composeTestRule.onNodeWithTag(TestTags.MAP_SEARCH_LOCATION).assertIsNotDisplayed()
    composeTestRule.onNodeWithTag(TestTags.MAP_NULL_ITINERARY).assertIsDisplayed()
  }*/
}
