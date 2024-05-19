package com.github.wanderwise_inc.app.e2e

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
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
        
        // verify that we have correctly navigated to overview screen
        composeTestRule.onNodeWithTag(TestTags.OVERVIEW_SCREEN).assertIsDisplayed()
        composeTestRule.onNodeWithTag(TestTags.BOTTOM_NAV).assertIsDisplayed()
        composeTestRule.onNodeWithTag(TopLevelRoute.CREATION).performClick()
        
        // verify that we have correctly navigated to the itinerary start creation screen
        composeTestRule.onNodeWithTag(TestTags.NO_NEW_CREATION_SCREEN).assertIsDisplayed()
        composeTestRule.onNodeWithTag(TestTags.START_ITINERARY_CREATION_BUTTON).performClick()
        
        // verify that we have indeed started creating an itinerary
        composeTestRule.onNodeWithTag(TestTags.MAP_NULL_USER_LOCATION).assertIsDisplayed()
        composeTestRule.onNodeWithTag(TestTags.NEW_CREATION_SCREEN).assertIsDisplayed()
        composeTestRule.onNodeWithTag(TestTags.CREATE_ITINERARY_BY_TRACKING_BUTTON).performClick()
        
        // verify that we have reached the UI for tracking the user
        composeTestRule.onNodeWithTag(TestTags.CREATE_ITINERARY_BY_TRACKING_SCREEN).assertIsDisplayed()
        composeTestRule.onNodeWithTag(TestTags.START_BUTTON).performClick() // start tracking
        composeTestRule.waitUntil(5000L) { true } // track for 5 seconds
        composeTestRule.onNodeWithTag(TestTags.STOP_BUTTON).performClick()
        composeTestRule.onNodeWithTag(TestTags.BACK_BUTTON).performClick()
        
        // verify that we have returned to choose the itinerary creation method screen
        composeTestRule.onNodeWithTag(TestTags.NEW_CREATION_SCREEN).assertIsDisplayed()
        composeTestRule.onNodeWithTag(TestTags.CREATE_ITINERARY_MANUALLY_BUTTON).performClick()
        
        //verify that we have reached the UI for manually creating an itinerary
        composeTestRule.onNodeWithTag(TestTags.CREATE_ITINERARY_MANUALLY_SCREEN).assertIsDisplayed()
        composeTestRule.onNodeWithTag(TestTags.CREATE_ITINERARY_LOCATION).assertExists()

    }
}