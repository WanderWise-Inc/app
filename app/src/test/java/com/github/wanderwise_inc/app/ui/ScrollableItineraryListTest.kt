package com.github.wanderwise_inc.app.ui

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.ComposeTestRule
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.github.wanderwise_inc.app.model.location.FakeItinerary
import com.github.wanderwise_inc.app.model.location.Itinerary
import com.github.wanderwise_inc.app.ui.list_itineraries.ItinerariesListScrollable
import com.github.wanderwise_inc.app.ui.list_itineraries.ItineraryListParent
import com.github.wanderwise_inc.app.viewmodel.MapViewModel
import com.github.wanderwise_inc.app.viewmodel.ProfileViewModel
import com.google.firebase.FirebaseApp
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
    
    private val paddingValues: PaddingValues = PaddingValues(0.dp)
    
    private lateinit var testItineraries:List<Itinerary>
    
    @Before
    fun setup() {
        MockKAnnotations.init(this)
        every { profileViewModel.checkIfItineraryIsLiked(any(), any()) } returns false
    }
    
    @Test
    fun `verify ItinerariesListScrollable displays all correct ItineraryBanners`() {
        // cannot add more itineraries as they would all have 
        // the same uid (not initialized with setItinerary())
        // => the assertion would fail as more than 1 tag will 
        // be found
        testItineraries = listOf(FakeItinerary.SWITZERLAND)
        composeTestRule.setContent {
            FirebaseApp.initializeApp(LocalContext.current)
            ItinerariesListScrollable(
                itineraries = testItineraries,
                mapViewModel = mapViewModel,
                profileViewModel = profileViewModel,
                navController = navController,
                paddingValues = paddingValues,
                parent = ItineraryListParent.PROFILE   // could be any parent
            )
        }
        
        composeTestRule.onNodeWithTag(TestTags.ITINERARY_LIST_SCROLLABLE).assertIsDisplayed()
        for (itinerary in testItineraries){
            composeTestRule.onNodeWithTag("${TestTags.ITINERARY_BANNER}_${itinerary.uid}").assertIsDisplayed()
        }
    }
    
    @Test
    fun `verify ItinerariesListScrollable prints correct message if list is empty`() {
        testItineraries = listOf() // empty list
        composeTestRule.setContent {
            //FirebaseApp.initializeApp(LocalContext.current)
            ItinerariesListScrollable(
                itineraries = testItineraries,
                mapViewModel = mapViewModel,
                profileViewModel = profileViewModel,
                navController = navController,
                paddingValues = paddingValues,
                parent = ItineraryListParent.PROFILE   // could be any parent
            )
        }

        composeTestRule.onNodeWithTag(TestTags.ITINERARY_LIST_NULL).assertIsDisplayed()
    }
    
}