package com.github.wanderwise_inc.app.ui.list_itineraries

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performScrollToIndex
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.github.wanderwise_inc.app.model.location.FakeItinerary
import com.github.wanderwise_inc.app.model.location.Itinerary
import com.github.wanderwise_inc.app.ui.TestTags
import com.github.wanderwise_inc.app.viewmodel.MapViewModel
import com.github.wanderwise_inc.app.viewmodel.ProfileViewModel
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class ScrollableItineraryListTest {
  @get:Rule val composeTestRule = createComposeRule()

  @MockK private lateinit var mapViewModel: MapViewModel
  @MockK private lateinit var profileViewModel: ProfileViewModel
  @MockK private lateinit var navController: NavHostController
  @MockK private lateinit var firebaseAuth: FirebaseAuth

  private val paddingValues: PaddingValues = PaddingValues(0.dp)

  private lateinit var testItineraries: List<Itinerary>

  @Before
  fun setup() {
    MockKAnnotations.init(this)
    every { profileViewModel.checkIfItineraryIsLiked(any(), any()) } returns false
    every { firebaseAuth.currentUser?.uid } returns null
  }

  @Test
  fun `verify ItinerariesListScrollable displays all correct ItineraryBanners`() {
    // cannot add more itineraries as they would all have
    // the same uid (not initialized with setItinerary())
    // => the assertion would fail as more than 1 tag will
    // be found
    testItineraries =
        listOf(FakeItinerary.SWITZERLAND, FakeItinerary.SAN_FRANCISCO, FakeItinerary.TOKYO)
    for (itinerary in testItineraries) {
      itinerary.uid = itinerary.title
    }
    composeTestRule.setContent {
      FirebaseApp.initializeApp(LocalContext.current)
      ItinerariesListScrollable(
          itineraries = testItineraries,
          mapViewModel = mapViewModel,
          profileViewModel = profileViewModel,
          navController = navController,
          firebaseAuth = firebaseAuth,
          paddingValues = paddingValues,
          parent = ItineraryListParent.PROFILE,
          // could be any parent
      )
    }

    //        composeTestRule.onNodeWithTag(TestTags.ITINERARY_LIST_SCROLLABLE).assertIsDisplayed()
    //
    // composeTestRule.onNodeWithTag("${TestTags.ITINERARY_BANNER}_${testItineraries[0].uid}").assertIsDisplayed()
    //
    // composeTestRule.onNodeWithTag("${TestTags.ITINERARY_BANNER}_${testItineraries[1].uid}").assertIsDisplayed()
    //
    // composeTestRule.onNodeWithTag(TestTags.ITINERARY_LIST_SCROLLABLE).performScrollToIndex(2)//.assertExists()//performScrollTo().assertIsDisplayed()
    //
    // composeTestRule.onNodeWithTag("${TestTags.ITINERARY_BANNER}_${testItineraries[2].uid}").assertIsDisplayed()
    for (i in testItineraries.indices) {
      composeTestRule.onNodeWithTag(TestTags.ITINERARY_LIST_SCROLLABLE).performScrollToIndex(i)
      composeTestRule
          .onNodeWithTag("${TestTags.ITINERARY_BANNER}_${testItineraries[i].uid}")
          .assertIsDisplayed()
    }
  }

  @Test
  fun `verify ItinerariesListScrollable prints correct message if list is empty`() {
    testItineraries = listOf() // empty list
    composeTestRule.setContent {
      // FirebaseApp.initializeApp(LocalContext.current)
      ItinerariesListScrollable(
          itineraries = testItineraries,
          mapViewModel = mapViewModel,
          profileViewModel = profileViewModel,
          navController = navController,
          firebaseAuth = firebaseAuth,
          paddingValues = paddingValues,
          parent = ItineraryListParent.PROFILE,
          // could be any parent
      )
    }

    composeTestRule.onNodeWithTag(TestTags.ITINERARY_LIST_NULL).assertIsDisplayed()
  }
}
