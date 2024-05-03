package com.github.wanderwise_inc.app.ui.list_itineraries

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.test.SemanticsNodeInteraction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollToIndex
import androidx.compose.ui.test.printToString
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.github.wanderwise_inc.app.model.location.FakeItinerary
import com.github.wanderwise_inc.app.model.location.Itinerary
import com.github.wanderwise_inc.app.printToLog
import com.github.wanderwise_inc.app.ui.TestTags
import com.github.wanderwise_inc.app.viewmodel.MapViewModel
import com.github.wanderwise_inc.app.viewmodel.ProfileViewModel
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import junit.framework.TestCase.assertEquals
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
  
  var itinerariesAreLiked = false

  @Before
  fun setup() {
    MockKAnnotations.init(this)
    every { firebaseAuth.currentUser?.uid } returns null
  }

  @Test
  fun `verify ItinerariesListScrollable displays all correct ItineraryBanners`() {
    testItineraries =
        listOf(FakeItinerary.SWITZERLAND, FakeItinerary.SAN_FRANCISCO, FakeItinerary.TOKYO)
    for (itinerary in testItineraries) {
      itinerary.uid = itinerary.title
    }
      
    every { profileViewModel.checkIfItineraryIsLiked(any(), any()) } returns false

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
    
  @Test
  fun `verify clicking on like button when itinerary is unliked correctly calls API`() {
      testItineraries = listOf(FakeItinerary.SWITZERLAND)

      every { profileViewModel.checkIfItineraryIsLiked(any(), any()) } returns false

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
      
      var itineraryLikesBackend = testItineraries.first().numLikes
      
      var likedItineraryListBackend = mutableListOf<String>()

      every { mapViewModel.incrementItineraryLikes(any()) } answers { itineraryLikesBackend++ }
      
      every { profileViewModel.addLikedItinerary(any(), any()) } answers { 
          likedItineraryListBackend.add(testItineraries.first().uid) 
      }
      
      //composeTestRule.onNodeWithTag("$")
      composeTestRule.onRoot(useUnmergedTree = true).printToLog()
      
      assertEquals(testItineraries.first().numLikes, itineraryLikesBackend)
      assertEquals(emptyList<String>(), likedItineraryListBackend)
      composeTestRule.onNodeWithTag(TestTags.ITINERARY_LIST_SCROLLABLE).performScrollToIndex(0)
      composeTestRule
          .onNodeWithTag("${TestTags.ITINERARY_BANNER_LIKE_BUTTON}_${testItineraries.first().uid}")
          .performClick()
      assertEquals(testItineraries.first().numLikes+1, itineraryLikesBackend)
      assertEquals(listOf(testItineraries.first().uid), likedItineraryListBackend)
  }

  @Test
  fun `verify clicking on like button when itinerary is liked correctly calls API`() {
    testItineraries = listOf(FakeItinerary.SWITZERLAND)

    every { profileViewModel.checkIfItineraryIsLiked(any(), any()) } returns true
      
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

    var itineraryLikesBackend = testItineraries.first().numLikes

    var likedItineraryListBackend = mutableListOf<String>(testItineraries.first().uid)

    every { mapViewModel.decrementItineraryLikes(any()) } answers { itineraryLikesBackend-- }

    every { profileViewModel.removeLikedItinerary(any(), any()) } answers {
        likedItineraryListBackend.remove(testItineraries.first().uid)
    }

    // composeTestRule.onRoot(useUnmergedTree = true).printToLog()

    assertEquals(testItineraries.first().numLikes, itineraryLikesBackend)
    assertEquals(listOf(testItineraries.first().uid), likedItineraryListBackend)
    composeTestRule.onNodeWithTag(TestTags.ITINERARY_LIST_SCROLLABLE).performScrollToIndex(0)
    composeTestRule
        .onNodeWithTag("${TestTags.ITINERARY_BANNER_LIKE_BUTTON}_${testItineraries.first().uid}")
        .performClick()
    assertEquals(testItineraries.first().numLikes-1, itineraryLikesBackend)
    assertEquals(listOf<String>(), likedItineraryListBackend)
  }
}
