package com.github.wanderwise_inc.app.ui

import android.app.Application
import android.content.Context
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.isDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.github.wanderwise_inc.app.data.DirectionsRepository
import com.github.wanderwise_inc.app.data.ImageRepository
import com.github.wanderwise_inc.app.data.ItineraryRepository
import com.github.wanderwise_inc.app.data.ItineraryRepositoryTestImpl
import com.github.wanderwise_inc.app.data.ProfileRepository
import com.github.wanderwise_inc.app.data.ProfileRepositoryTestImpl
import com.github.wanderwise_inc.app.model.location.FakeItinerary
import com.github.wanderwise_inc.app.model.location.Itinerary
import com.github.wanderwise_inc.app.model.location.ItineraryTags
import com.github.wanderwise_inc.app.model.location.Location
import com.github.wanderwise_inc.app.model.profile.Profile
import com.github.wanderwise_inc.app.ui.list_itineraries.ItinerariesListScrollable
import com.github.wanderwise_inc.app.ui.list_itineraries.ItineraryListParent
import com.github.wanderwise_inc.app.ui.list_itineraries.OverviewScreen
import com.github.wanderwise_inc.app.viewmodel.MapViewModel
import com.github.wanderwise_inc.app.viewmodel.ProfileViewModel
import com.github.wanderwise_inc.app.viewmodel.UserLocationClient
import com.google.firebase.FirebaseApp
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.mock
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class OverviewScreenTest {
  @get:Rule val composeTestRule = createComposeRule()

  @MockK
  private lateinit var mapViewModel: MapViewModel
  @MockK
  private lateinit var profileViewModel: ProfileViewModel
  @MockK
  private lateinit var navController: NavHostController

  private lateinit var testItineraries:List<Itinerary>

  @Before
  fun setup() {
    MockKAnnotations.init(this)
    
    every { profileViewModel.checkIfItineraryIsLiked(any(), any()) } returns false
    
    composeTestRule.setContent { 
      FirebaseApp.initializeApp(LocalContext.current)
      OverviewScreen(mapViewModel, profileViewModel, navController)
    }
  }
  
  @Test
  fun `search bar should be displayed on liked screen`() {
    composeTestRule.onNodeWithTag(TestTags.SEARCH_BAR).assertIsDisplayed()
  }
  
  @Test
  fun `category selector should be displayed on liked screen`() {
    composeTestRule.onNodeWithTag(TestTags.CATEGORY_SELECTOR).assertIsDisplayed()
  }

  @Test
  fun `itinerary_list should be displayed on liked screen`() {
    composeTestRule.onNodeWithTag(TestTags.ITINERARY_LIST_SCROLLABLE).assertIsDisplayed()
  }

  @Test
  fun `at least one itinerary should be displayed`() {
    composeTestRule
        .onNodeWithTag("${TestTags.ITINERARY_BANNER}_${testItineraries[0].uid}")
        .isDisplayed()
  }
}
