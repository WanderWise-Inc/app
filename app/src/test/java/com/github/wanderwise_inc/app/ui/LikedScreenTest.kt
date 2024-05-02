package com.github.wanderwise_inc.app.ui

import androidx.compose.runtime.MutableState
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.test.SemanticsNodeInteraction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performImeAction
import androidx.compose.ui.test.performScrollToIndex
import androidx.compose.ui.test.performSemanticsAction
import androidx.compose.ui.test.performTextClearance
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.printToString
import androidx.navigation.NavHostController
import com.github.wanderwise_inc.app.model.location.FakeItinerary
import com.github.wanderwise_inc.app.model.location.Itinerary
import com.github.wanderwise_inc.app.model.location.Tag
import com.github.wanderwise_inc.app.ui.list_itineraries.DisplayOverviewItineraries
import com.github.wanderwise_inc.app.ui.list_itineraries.LikedScreen
import com.github.wanderwise_inc.app.ui.list_itineraries.OverviewScreen
import com.github.wanderwise_inc.app.viewmodel.MapViewModel
import com.github.wanderwise_inc.app.viewmodel.ProfileViewModel
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.verify
import kotlinx.coroutines.flow.flow
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class LikedScreenTest {
  @get:Rule val composeTestRule = createComposeRule()

  @MockK private lateinit var mapViewModel: MapViewModel
  @MockK private lateinit var profileViewModel: ProfileViewModel
  @MockK private lateinit var navController: NavHostController
  @MockK private lateinit var firebaseAuth: FirebaseAuth

//  @MockK private lateinit var sliderPositionPriceState: MutableState<ClosedFloatingPointRange<Float>>
//  @MockK private lateinit var sliderPositionTimeState: MutableState<ClosedFloatingPointRange<Float>>

  private val testItineraries = listOf(
    FakeItinerary.SAN_FRANCISCO,
    FakeItinerary.SWITZERLAND,
    FakeItinerary.TOKYO
  )

  @Before
  fun setup() {
    MockKAnnotations.init(this)

    for (itinerary in testItineraries) {
      itinerary.uid = itinerary.title // set uid for testTags
    }

    every { firebaseAuth.currentUser?.uid } returns null

    every { profileViewModel.checkIfItineraryIsLiked(any(), any()) } returns false
    every { profileViewModel.getLikedItineraries(any()) } returns flow { emit(listOf("0", "1", "2")) }

    every { mapViewModel.getItineraryFromUids(any()) } returns flow { emit(testItineraries) }

    composeTestRule.setContent {
      FirebaseApp.initializeApp(LocalContext.current)
      LikedScreen(
        mapViewModel,
        profileViewModel,
        navController,
        firebaseAuth,
      )
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
  fun `category tags filter itineraries correctly`() {
    // basic tag is Adventure, which eliminates Tokyo
    testExpectedItinerariesDisplayed(listOf(FakeItinerary.SWITZERLAND, FakeItinerary.SAN_FRANCISCO))

    // Tag Luxury should eliminate all Itineraries
    testExpectedItinerariesDisplayed(listOf(), selectedTagIndex = 1)

    // Tag Photography should result in only Tokyo
    testExpectedItinerariesDisplayed(listOf(FakeItinerary.TOKYO), selectedTagIndex = 2)
  }

  @Test
  fun `search query filters itineraries correctly`() { // basic tag is Adventure, which eliminates Tokyo
    // SF and Switzerland itineraries should be displayed
    testExpectedItinerariesDisplayed(listOf(FakeItinerary.SWITZERLAND, FakeItinerary.SAN_FRANCISCO))

    // no itineraries should be displayed
    testExpectedItinerariesDisplayed(listOf(), textInput = "Tokyo")

    // should only correspond to SF and Switzerland itineraries
    testExpectedItinerariesDisplayed(listOf(FakeItinerary.SWITZERLAND), textInput = "Switzerland")
  }

  // Was not able to make this test work yet, interaction with CategorySelector is really annoying
  /*@Test
  fun `price range filters itineraries correctly`() { 
    // basic tag is Adventure, which eliminates Tokyo
    // SF -> 5$, Switzerland -> 50$, Tokyo -> 
    
    // choose price range
    var priceRange = 0f..100f
    every { sliderPositionPriceState.value } returns priceRange
    
    // SF and Switzerland itineraries should be displayed
    testExpectedItinerariesDisplayed(listOf(FakeItinerary.SWITZERLAND, FakeItinerary.SAN_FRANCISCO))

    priceRange = 40f..100f
    every { sliderPositionPriceState.value } returns priceRange
    // no itineraries should be displayed
    testExpectedItinerariesDisplayed(listOf(FakeItinerary.SWITZERLAND))

    priceRange = 0f..30f
    every { sliderPositionPriceState.value } returns priceRange
    // should only correspond to SF and Switzerland itineraries
    testExpectedItinerariesDisplayed(listOf(FakeItinerary.SAN_FRANCISCO))
  }*/

  private fun testExpectedItinerariesDisplayed(
    expectedItineraries: List<Itinerary>,
    selectedTagIndex: Int = 0,
    textInput: String = "",
  ) {
    // choose corresponding tag for search (0 = Adventure, 1 = Luxury, 2 = Photography, 3 = Foodie)
    composeTestRule.onNodeWithTag("${TestTags.CATEGORY_SELECTOR_TAB}_$selectedTagIndex").performClick()
    // enter text query for search
    composeTestRule.onNodeWithTag(TestTags.SEARCH_BAR).performTextClearance()
    if (textInput.isNotBlank()) { // search for itineraries
      composeTestRule.onNodeWithTag(TestTags.SEARCH_BAR).performTextInput(textInput)
    }

    if (expectedItineraries.isEmpty()) {
      composeTestRule.onNodeWithTag(TestTags.ITINERARY_LIST_NULL).assertIsDisplayed()
    } else {
      //composeTestRule.onRoot(useUnmergedTree = true).printToLog()
      for (i in expectedItineraries.indices) {
        composeTestRule
          .onNodeWithTag(TestTags.ITINERARY_LIST_SCROLLABLE)
          .performScrollToIndex(i) // scroll to correct position
        composeTestRule
          .onNodeWithTag("${TestTags.ITINERARY_BANNER}_${expectedItineraries[i].uid}")
          .assertIsDisplayed()
      }
      for (itinerary in testItineraries.minus(expectedItineraries)) {
        composeTestRule
          .onNodeWithTag("${TestTags.ITINERARY_BANNER}_${itinerary.uid}")
          .assertDoesNotExist()
      }
    }
  }
}
