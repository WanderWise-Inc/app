package com.github.wanderwise_inc.app.e2e

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.isDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performImeAction
import androidx.compose.ui.test.performScrollToIndex
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.GrantPermissionRule
import com.github.wanderwise_inc.app.MainActivity
import com.github.wanderwise_inc.app.ui.TestTags
import com.github.wanderwise_inc.app.ui.navigation.TopLevelRoute
import org.junit.Rule
import org.junit.jupiter.api.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CreateItineraryE2ETest {
    @get:Rule val activityRule = ActivityScenarioRule(MainActivity::class.java)
    @get:Rule val composeTestRule = createAndroidComposeRule(MainActivity::class.java)
    @get:Rule
    val grantPermissionRule: GrantPermissionRule =
        GrantPermissionRule.grant(
            android.Manifest.permission.ACCESS_FINE_LOCATION,
            android.Manifest.permission.ACCESS_COARSE_LOCATION
        )
    
    @Test
    fun createItineraryFlowTest() {
        // verify that we are on the login screen
        composeTestRule.onNodeWithTag(TestTags.SIGN_IN_BUTTON).assertIsDisplayed()
        composeTestRule.onNodeWithTag(TestTags.SIGN_IN_BUTTON).performClick()
        composeTestRule.waitUntil { composeTestRule.onNodeWithTag(TestTags.OVERVIEW_SCREEN).isDisplayed() }

        // verify that we have correctly navigated to overview screen
        composeTestRule.onNodeWithTag(TestTags.OVERVIEW_SCREEN).assertIsDisplayed()
        composeTestRule.onNodeWithTag(TestTags.BOTTOM_NAV).assertIsDisplayed()
        composeTestRule.onNodeWithTag(TestTags.BOTTOM_NAV_CREATION).performClick()
        composeTestRule.waitForIdle()
        
        // verify that we have correctly navigated to the itinerary start creation screen
        composeTestRule.onNodeWithTag(TestTags.NO_NEW_CREATION_SCREEN).assertIsDisplayed()
        composeTestRule.onNodeWithTag(TestTags.START_ITINERARY_CREATION_BUTTON).performClick()
        composeTestRule.waitForIdle()
        
        // verify that we have indeed started creating an itinerary
        composeTestRule.onNodeWithTag(TestTags.MAP_NULL_USER_LOCATION).assertIsDisplayed()
        composeTestRule.onNodeWithTag(TestTags.NEW_CREATION_SCREEN).assertIsDisplayed()
        composeTestRule.onNodeWithTag(TestTags.CREATE_ITINERARY_BY_TRACKING_BUTTON).performClick()
        composeTestRule.waitForIdle()
        
        // verify that we have reached the UI for tracking the user
        composeTestRule.onNodeWithTag(TestTags.CREATE_ITINERARY_BY_TRACKING_SCREEN).assertIsDisplayed()
        composeTestRule.onNodeWithTag(TestTags.START_BUTTON).performClick() // start tracking
        composeTestRule.waitUntil(5000L) { true } // track for 5 seconds
        composeTestRule.onNodeWithTag(TestTags.STOP_BUTTON).performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag(TestTags.BACK_BUTTON).performClick()
        composeTestRule.waitForIdle()
        
        // verify that we have returned to choose the itinerary creation method screen
        composeTestRule.onNodeWithTag(TestTags.NEW_CREATION_SCREEN).assertIsDisplayed()
        composeTestRule.onNodeWithTag(TestTags.CREATE_ITINERARY_MANUALLY_BUTTON).performClick()
        composeTestRule.waitForIdle()
        
        // verify that we have reached the UI for manually creating an itinerary
        composeTestRule.onNodeWithTag(TestTags.CREATE_ITINERARY_MANUALLY_SCREEN).assertIsDisplayed()
        composeTestRule.onNodeWithTag("${TestTags.CREATE_ITINERARY_LOCATION}_Placed marker").assertExists()
        composeTestRule.onNodeWithTag(TestTags.ADD_LOCATION_BUTTON).performClick()
        composeTestRule.waitForIdle()
        
        // verify that we have navigated to the search location screen
        composeTestRule.onNodeWithTag(TestTags.LOCATION_SEARCH_SCAFFOLD).assertIsDisplayed()
        composeTestRule.onNodeWithTag(TestTags.LOCATION_SEARCH_BAR).performTextInput("0")
        composeTestRule.onNodeWithTag(TestTags.LOCATION_SEARCH_BAR).performImeAction()
        composeTestRule.waitForIdle()
        
        // verify that some results have been found
        composeTestRule.onNodeWithTag(TestTags.LOCATION_SEARCH_RESULTS).assertIsDisplayed()
        composeTestRule.onNodeWithTag(TestTags.LOCATION_SEARCH_RESULTS).performScrollToIndex(0)
        composeTestRule.onNodeWithTag("${TestTags.LOCATION_SEARCH_RESULTS}_0").performClick()
        composeTestRule.waitForIdle()
        
        // verify that the add location button has appeared and click it
        composeTestRule.onNodeWithTag(TestTags.ADD_LOCATION_BUTTON).assertIsDisplayed()
        composeTestRule.onNodeWithTag(TestTags.ADD_LOCATION_BUTTON).performClick()
        composeTestRule.waitForIdle()
        
        // verify that we have navigated back to itinerary creation method screen
        composeTestRule.onNodeWithTag(TestTags.CREATE_ITINERARY_MANUALLY_SCREEN).assertIsDisplayed()
        composeTestRule.onNodeWithTag("${TestTags.CREATE_ITINERARY_LOCATION}_0").assertExists()
        
        // click on next step of itinerary creation
        composeTestRule.onNodeWithTag(TestTags.ITINERARY_CREATION_BAR_DESCRIPTION).performClick()
        composeTestRule.waitForIdle()
        
        // verify that we have navigated to choose description screen
        composeTestRule.onNodeWithTag(TestTags.CREATION_SCREEN_DESCRIPTION_TITLE).assertIsDisplayed()
        composeTestRule.onNodeWithTag(TestTags.CREATION_SCREEN_TITLE).performTextInput("My title")
        composeTestRule.onNodeWithTag(TestTags.CREATION_SCREEN_DESCRIPTION).performTextInput("My description")
        composeTestRule.onNodeWithTag(TestTags.ITINERARY_CREATION_BAR_TAGS).performClick()
        composeTestRule.waitForIdle()
        
        // verify that we have navigated to choose tags screen
        composeTestRule.onNodeWithTag(TestTags.CREATION_SCREEN_TAGS).assertIsDisplayed()
        composeTestRule.onNodeWithTag(TestTags.ITINERARY_CREATION_PRICE_ESTIMATION).performTextInput("10")
        composeTestRule.onNodeWithTag(TestTags.ITINERARY_CREATION_TIME_ESTIMATION).performTextInput("2")
        composeTestRule.onNodeWithTag(TestTags.ITINERARY_CREATION_TAGS).performClick()
        composeTestRule.onNodeWithTag("${TestTags.ITINERARY_CREATION_TAGS}_Adventure").performClick() // add adventure tag
        
        // click on next step of itinerary creation
        composeTestRule.onNodeWithTag(TestTags.ITINERARY_CREATION_BAR_PREVIEW).performClick()
        composeTestRule.waitForIdle()
        
        // verify that we have navigated to preview itinerary banner screen
        composeTestRule.onNodeWithTag(TestTags.CREATION_SCREEN_PREVIEW).assertIsDisplayed()
        composeTestRule.onNodeWithTag(TestTags.CREATION_SCREEN_PREVIEW_BANNER).assertIsDisplayed()
        composeTestRule.onNodeWithTag(TestTags.CREATION_SCREEN_PREVIEW_BUTTON).performClick()
        composeTestRule.waitForIdle()
        
        // verify that we have navigated to preview itinerary screen
        composeTestRule.onNodeWithTag(TestTags.CREATION_SCREEN_PREVIEW_ITINERARY).assertIsDisplayed()
        composeTestRule.onNodeWithTag(TestTags.CREATION_FINISH_BUTTON).performClick()
        composeTestRule.waitForIdle()
        
        // verify that we have navigated to profile screen
        composeTestRule.onNodeWithTag(TestTags.PROFILE_SCREEN).assertIsDisplayed()
        composeTestRule.onNodeWithTag("${TestTags.ITINERARY_BANNER}_").assertExists() // verify that the created itinerary exists
    }
}