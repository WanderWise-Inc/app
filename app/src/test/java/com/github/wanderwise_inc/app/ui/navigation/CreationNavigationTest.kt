package com.github.wanderwise_inc.app.ui.navigation

import android.location.Location
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.lifecycle.liveData
import androidx.navigation.compose.ComposeNavigator
import androidx.navigation.testing.TestNavHostController
import com.github.wanderwise_inc.app.data.ImageRepository
import com.github.wanderwise_inc.app.model.location.FakeItinerary
import com.github.wanderwise_inc.app.model.profile.Profile
import com.github.wanderwise_inc.app.ui.TestTags
import com.github.wanderwise_inc.app.ui.creation.CreationScreen
import com.github.wanderwise_inc.app.ui.creation.steps.CreationStepPreview
import com.github.wanderwise_inc.app.ui.home.HomeScreen
import com.github.wanderwise_inc.app.viewmodel.BottomNavigationViewModel
import com.github.wanderwise_inc.app.viewmodel.MapViewModel
import com.github.wanderwise_inc.app.viewmodel.ProfileViewModel
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.every
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.runner.RunWith
import org.mockito.kotlin.or
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class CreationNavigationTest {
    @get:Rule val composeTestRule = createComposeRule()

    @MockK private lateinit var imageRepository: ImageRepository
    @MockK private lateinit var mapViewModel: MapViewModel
    @MockK private lateinit var profileViewModel: ProfileViewModel
    @MockK private lateinit var bottomNavigationViewModel: BottomNavigationViewModel
    @MockK private lateinit var firebaseAuth: FirebaseAuth

    private lateinit var navController: TestNavHostController

    @Before
    fun setupNavHost() {
        MockKAnnotations.init(this)

        composeTestRule.setContent {
            /*val mockProfile = Profile("", "Test", "0", "Bio", null)
            val mockItinerary = FakeItinerary.SAN_FRANCISCO
            val mockLocation = Location("")

            every { imageRepository.fetchImage(any()) } returns flowOf(null)

            every { profileViewModel.getProfile(any()) } returns flow { emit(mockProfile) }
            coEvery { profileViewModel.setProfile(any()) } returns Unit
            every { profileViewModel.addLikedItinerary(any(), any()) } returns Unit
            every { profileViewModel.getLikedItineraries(any()) } returns flow { emit(emptyList()) }
            every { profileViewModel.getDefaultProfilePicture() } returns flow { emit(null) }
            every { profileViewModel.getProfilePicture(any()) } returns flow { emit(null) }
*/
            every { mapViewModel.setItinerary(any()) } returns Unit
            every { mapViewModel.incrementItineraryLikes(any()) } returns Unit
            every { mapViewModel.getAllPublicItineraries() } returns flow { emit(emptyList()) }
            every { mapViewModel.getUserLocation() } returns flow { emit(Location("")) }
            every { mapViewModel.getUserItineraries(any()) } returns flow { emit(emptyList()) }
            every { mapViewModel.getItineraryFromUids(any()) } returns flow { emit(emptyList()) }
            every { mapViewModel.getFocusedItinerary() } returns null
            // coEvery { mapViewModel.getItineraryFromUids(any()) } returns flow { listOf(mockItinerary) }

            /*every { bottomNavigationViewModel.setSelected(any()) } returns Unit
            every { bottomNavigationViewModel.selected } returns liveData { 0 }

            every { firebaseAuth.currentUser?.uid } returns null*/

            FirebaseApp.initializeApp(LocalContext.current)
            navController = TestNavHostController(LocalContext.current)
            navController.navigatorProvider.addNavigator(ComposeNavigator())
            
            CreationScreen(
                mapViewModel = mapViewModel,
                navController = navController
            )
        }
    }

    @Test
    fun `verify top navigation bar is displayed`() {
        composeTestRule.onNodeWithTag(TestTags.CREATION_SCREEN_NAV_BAR).assertIsDisplayed()
    }

    @Test
    fun `verify start destination is overview screen`() {
        composeTestRule.onNodeWithTag(TestTags.CREATION_SCREEN_LOCATIONS).assertIsDisplayed()

        val route = navController.currentBackStackEntry?.destination?.route
        assertEquals("Creation/${CreationStepsRoute.LOCATIONS}", route)
    }

    @Test
    fun `perform click on description navigates to description`() {
        composeTestRule.onNodeWithTag("Creation/${CreationStepsRoute.DESCRIPTION}").performClick()

        composeTestRule.onNodeWithTag(TestTags.CREATION_SCREEN_DESCRIPTION).assertIsDisplayed()

        val route = navController.currentBackStackEntry?.destination?.route
        assertEquals("Creation/${CreationStepsRoute.DESCRIPTION}", route)
    }

    @Test
    fun `perform click on tags navigates to tags`() {
        composeTestRule.onNodeWithTag("Creation/${CreationStepsRoute.TAGS}").performClick()

        composeTestRule.onNodeWithTag(TestTags.CREATION_SCREEN_TAGS).assertIsDisplayed()

        val route = navController.currentBackStackEntry?.destination?.route
        assertEquals("Creation/${CreationStepsRoute.TAGS}", route)
    }

    @Test
    fun `perform click on preview navigates to preview`() {
        composeTestRule.onNodeWithTag("Creation/${CreationStepsRoute.PREVIEW}").performClick()

        composeTestRule.onNodeWithTag(TestTags.CREATION_SCREEN_PREVIEW).assertIsDisplayed()

        val route = navController.currentBackStackEntry?.destination?.route
        assertEquals("Creation/${CreationStepsRoute.PREVIEW}", route)
    }
}

@RunWith(RobolectricTestRunner::class)
class CreationPreviewNavigationTest {
    @get:Rule val composeTestRule = createComposeRule()

    @MockK private lateinit var imageRepository: ImageRepository
    @MockK private lateinit var mapViewModel: MapViewModel
    @MockK private lateinit var profileViewModel: ProfileViewModel
    @MockK private lateinit var bottomNavigationViewModel: BottomNavigationViewModel
    @MockK private lateinit var firebaseAuth: FirebaseAuth

    private lateinit var navController: TestNavHostController

    @Before
    fun setupNavHost() {
        MockKAnnotations.init(this)

        composeTestRule.setContent {
            /*val mockProfile = Profile("", "Test", "0", "Bio", null)
            val mockItinerary = FakeItinerary.SAN_FRANCISCO
            val mockLocation = Location("")

            every { imageRepository.fetchImage(any()) } returns flowOf(null)

            every { profileViewModel.getProfile(any()) } returns flow { emit(mockProfile) }
            coEvery { profileViewModel.setProfile(any()) } returns Unit
            every { profileViewModel.addLikedItinerary(any(), any()) } returns Unit
            every { profileViewModel.getLikedItineraries(any()) } returns flow { emit(emptyList()) }
            every { profileViewModel.getDefaultProfilePicture() } returns flow { emit(null) }
            every { profileViewModel.getProfilePicture(any()) } returns flow { emit(null) }

            every { mapViewModel.setItinerary(any()) } returns Unit
            every { mapViewModel.incrementItineraryLikes(any()) } returns Unit
            every { mapViewModel.getAllPublicItineraries() } returns flow { emit(emptyList()) }
            every { mapViewModel.getUserLocation() } returns flow { emit(Location("")) }
            every { mapViewModel.getUserItineraries(any()) } returns flow { emit(emptyList()) }
            every { mapViewModel.getItineraryFromUids(any()) } returns flow { emit(emptyList()) }
            every { mapViewModel.getFocusedItinerary() } returns null*/
            // coEvery { mapViewModel.getItineraryFromUids(any()) } returns flow { listOf(mockItinerary) }

            /*every { bottomNavigationViewModel.setSelected(any()) } returns Unit
            every { bottomNavigationViewModel.selected } returns liveData { 0 }

            every { firebaseAuth.currentUser?.uid } returns null*/

            FirebaseApp.initializeApp(LocalContext.current)
            navController = TestNavHostController(LocalContext.current)
            navController.navigatorProvider.addNavigator(ComposeNavigator())

            CreationStepPreview(
                navController = navController
            )
        }
    }

    @Test
    fun `verify change preview floating button is displayed`() {
        composeTestRule.onNodeWithTag(TestTags.CREATION_SCREEN_PREVIEW_BUTTON).assertIsDisplayed()
    }

    @Test
    fun `verify start destination is preview banner screen`() {
        composeTestRule.onNodeWithTag(TestTags.CREATION_SCREEN_PREVIEW_BANNER).assertIsDisplayed()

        val route = navController.currentBackStackEntry?.destination?.route
        assertEquals("Creation/${CreationPreviewOptions.PREVIEW_BANNER}", route)
    }

    @Test
    fun `perform click on floating button navigates to preview itinerary`() {
        composeTestRule.onNodeWithTag(TestTags.CREATION_SCREEN_PREVIEW_BUTTON).performClick()

        composeTestRule.onNodeWithTag(TestTags.CREATION_SCREEN_PREVIEW_ITINERARY).assertIsDisplayed()

        val route = navController.currentBackStackEntry?.destination?.route
        assertEquals("Creation/${CreationPreviewOptions.PREVIEW_ITINERARY}", route)
    }

    @Test
    fun `perform click on floating button twice navigates to preview banner`() {
        composeTestRule.onNodeWithTag(TestTags.CREATION_SCREEN_PREVIEW_BUTTON).performClick()
        composeTestRule.onNodeWithTag(TestTags.CREATION_SCREEN_PREVIEW_BUTTON).performClick()

        composeTestRule.onNodeWithTag(TestTags.CREATION_SCREEN_PREVIEW_BANNER).assertIsDisplayed()

        val route = navController.currentBackStackEntry?.destination?.route
        assertEquals("Creation/${CreationPreviewOptions.PREVIEW_BANNER}", route)
    }
}
