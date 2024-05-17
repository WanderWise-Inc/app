package com.github.wanderwise_inc.app.ui.list_itineraries

import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollToIndex
import androidx.compose.ui.test.performTextClearance
import androidx.compose.ui.test.performTextInput
import androidx.navigation.NavHostController
import com.github.wanderwise_inc.app.data.ImageRepository
import com.github.wanderwise_inc.app.model.location.FakeItinerary
import com.github.wanderwise_inc.app.model.location.Itinerary
import com.github.wanderwise_inc.app.model.profile.Profile
import com.github.wanderwise_inc.app.ui.TestTags
import com.github.wanderwise_inc.app.viewmodel.ItineraryViewModel
import com.github.wanderwise_inc.app.viewmodel.ProfileViewModel
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.every
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.flow.flow
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class OverviewScreenTest {
  @get:Rule val composeTestRule = createComposeRule()

  @MockK private lateinit var itineraryViewModel: ItineraryViewModel
  @MockK private lateinit var profileViewModel: ProfileViewModel
  @MockK private lateinit var navController: NavHostController
  @MockK private lateinit var firebaseAuth: FirebaseAuth
  @MockK private lateinit var imageRepository: ImageRepository

  private var sliderPositionPriceState = mutableStateOf(0f..100f)
  private var sliderPositionTimeState = mutableStateOf(0f..24f)
  private val profile = Profile(userUid = "0", displayName = "me", bio = "bio")

  private val testItineraries =
      listOf(FakeItinerary.SAN_FRANCISCO, FakeItinerary.SWITZERLAND, FakeItinerary.TOKYO)

  @Before
  fun setup() {
    MockKAnnotations.init(this)

    every { imageRepository.fetchImage(any()) } returns flow { emit(null) }
    every { profileViewModel.getProfile(any()) } returns flow { emit(profile) }

    for (itinerary in testItineraries) {
      itinerary.uid = itinerary.title // set uid for testTags
    }

    coEvery { profileViewModel.checkIfItineraryIsLiked(any(), any()) } returns false
    every { profileViewModel.getUserUid() } returns "OverViewScreenTestUserUid"
    every { profileViewModel.getLikedItineraries(any()) } returns flow { emit(emptyList()) }

    every { itineraryViewModel.getAllPublicItineraries() } returns flow { emit(testItineraries) }

    composeTestRule.setContent {
      FirebaseApp.initializeApp(LocalContext.current)
      DisplayOverviewItineraries(
          itineraryViewModel,
          profileViewModel,
          navController,
          sliderPositionPriceState,
          sliderPositionTimeState,
          imageRepository)
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
  fun `search query filters itineraries correctly`() { // basic tag is Adventure, which eliminates
    // Tokyo
    // SF and Switzerland itineraries should be displayed
    testExpectedItinerariesDisplayed(listOf(FakeItinerary.SWITZERLAND, FakeItinerary.SAN_FRANCISCO))

    // no itineraries should be displayed
    testExpectedItinerariesDisplayed(listOf(), textInput = "Tokyo")

    // should only correspond to SF and Switzerland itineraries
    testExpectedItinerariesDisplayed(listOf(FakeItinerary.SWITZERLAND), textInput = "Switzerland")
  }

  @Test
  fun `price range filters itineraries correctly`() {
    // basic tag is Adventure, which eliminates Tokyo
    // SF -> 5$, Switzerland -> 50$, Tokyo -> 20$

    // choose price range
    sliderPositionPriceState.value = 0f..100f

    // SF and Switzerland itineraries should be displayed
    testExpectedItinerariesDisplayed(listOf(FakeItinerary.SWITZERLAND, FakeItinerary.SAN_FRANCISCO))

    testExpectedItinerariesDisplayed(listOf(FakeItinerary.SWITZERLAND), priceRange = 40f..100f)

    testExpectedItinerariesDisplayed(listOf(FakeItinerary.SAN_FRANCISCO), priceRange = 0f..30f)
  }

  @Test
  fun `time range filters itineraries correctly`() {
    // basic tag is Adventure, which eliminates Tokyo
    // SF -> 3h, Switzerland -> 10h, Tokyo -> 4h

    // SF and Switzerland itineraries should be displayed
    testExpectedItinerariesDisplayed(listOf(FakeItinerary.SWITZERLAND, FakeItinerary.SAN_FRANCISCO))

    testExpectedItinerariesDisplayed(listOf(FakeItinerary.SWITZERLAND), timeRange = 5f..12f)

    testExpectedItinerariesDisplayed(listOf(FakeItinerary.SAN_FRANCISCO), timeRange = 1f..3.5f)
  }

  private fun testExpectedItinerariesDisplayed(
      expectedItineraries: List<Itinerary>,
      selectedTagIndex: Int = 0,
      textInput: String = "",
      priceRange: ClosedFloatingPointRange<Float> = 0f..100f,
      timeRange: ClosedFloatingPointRange<Float> = 0f..24f
  ) {
    // choose corresponding tag for search (0 = Adventure, 1 = Luxury, 2 = Photography, 3 = Foodie)
    composeTestRule
        .onNodeWithTag("${TestTags.CATEGORY_SELECTOR_TAB}_$selectedTagIndex")
        .performClick()
    // enter text query for search
    composeTestRule.onNodeWithTag(TestTags.SEARCH_BAR).performTextClearance()
    if (textInput.isNotBlank()) { // search for itineraries
      composeTestRule.onNodeWithTag(TestTags.SEARCH_BAR).performTextInput(textInput)
    }
    // choose price range
    sliderPositionPriceState.value = priceRange
    // choose time range
    sliderPositionTimeState.value = timeRange

    if (expectedItineraries.isEmpty()) {
      composeTestRule.onNodeWithTag(TestTags.ITINERARY_LIST_NULL).assertIsDisplayed()
    } else {
      // composeTestRule.onRoot(useUnmergedTree = true).printToLog()
      for (i in expectedItineraries.indices) {
        composeTestRule
            .onNodeWithTag(TestTags.ITINERARY_LIST_SCROLLABLE)
            .performScrollToIndex(i) // scroll to correct position
        composeTestRule
            .onNodeWithTag("${TestTags.ITINERARY_BANNER}_${expectedItineraries[i].uid}")
            .assertIsDisplayed()
      }
      for (itinerary in testItineraries.minus(expectedItineraries.toSet())) {
        composeTestRule
            .onNodeWithTag("${TestTags.ITINERARY_BANNER}_${itinerary.uid}")
            .assertDoesNotExist()
      }
    }
  }
}
