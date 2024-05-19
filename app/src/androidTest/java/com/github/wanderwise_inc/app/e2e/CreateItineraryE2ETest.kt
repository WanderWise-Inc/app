package com.github.wanderwise_inc.app.e2e

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.isDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onFirst
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performImeAction
import androidx.compose.ui.test.performScrollToIndex
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.GrantPermissionRule
import com.github.wanderwise_inc.app.MainActivity
import com.github.wanderwise_inc.app.model.location.Itinerary
import com.github.wanderwise_inc.app.model.location.Location
import com.github.wanderwise_inc.app.ui.TestTags
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CreateItineraryE2ETest {
  @get:Rule val activityRule = ActivityScenarioRule(MainActivity::class.java)
  @get:Rule val composeTestRule = createAndroidComposeRule(MainActivity::class.java)
  @get:Rule
  val grantPermissionRule: GrantPermissionRule =
      GrantPermissionRule.grant(
          android.Manifest.permission.ACCESS_FINE_LOCATION,
          android.Manifest.permission.ACCESS_COARSE_LOCATION)

  // approximate itinerary being created (correct up to waypoints added)
  val itinerary: Itinerary =
      Itinerary.Builder(
              uid = "",
              userUid = "0",
              title = "GooglePlex tour",
              tags = mutableListOf("Adventure"),
              description = "Tour around the Google HQ",
              price = 20f,
              time = 2)
          .build()

  // location returned by search
  val location: Location =
      Location(
          lat = 37.419000999999994,
          long = -122.08237596053958,
          title = "Google",
          address = "Mountain View, Santa Clara County, California, United States")

  @Test
  fun createItineraryFlowTest() {
    // verify that we are on the login screen
    composeTestRule.onNodeWithTag(TestTags.SIGN_IN_BUTTON).assertIsDisplayed()
    composeTestRule.onNodeWithTag(TestTags.SIGN_IN_BUTTON).performClick()
    composeTestRule.waitUntil {
      composeTestRule.onNodeWithTag(TestTags.OVERVIEW_SCREEN).isDisplayed()
    }

    // verify that we have correctly navigated to overview screen
    composeTestRule.onNodeWithTag(TestTags.OVERVIEW_SCREEN).assertIsDisplayed()
    composeTestRule.onNodeWithTag(TestTags.BOTTOM_NAV).assertIsDisplayed()
    composeTestRule
        .onNodeWithTag(TestTags.BOTTOM_NAV_CREATION, useUnmergedTree = true)
        .performClick()
    composeTestRule.waitForIdle()

    // verify that we have correctly navigated to the itinerary start creation screen
    composeTestRule.onNodeWithTag(TestTags.NO_NEW_CREATION_SCREEN).assertIsDisplayed()
    composeTestRule
        .onNodeWithTag(TestTags.START_ITINERARY_CREATION_BUTTON, useUnmergedTree = true)
        .performClick()
    composeTestRule.waitForIdle()

    // verify that we have indeed started creating an itinerary
    composeTestRule.onNodeWithTag(TestTags.MAP_NULL_USER_LOCATION).assertIsDisplayed()
    composeTestRule.onNodeWithTag(TestTags.NEW_CREATION_SCREEN).assertIsDisplayed()
    composeTestRule.onNodeWithTag(TestTags.CREATE_ITINERARY_BY_TRACKING_BUTTON).performClick()
    composeTestRule.waitForIdle()

    // verify that we have reached the UI for tracking the user
    composeTestRule.onNodeWithTag(TestTags.CREATE_ITINERARY_BY_TRACKING_SCREEN).assertIsDisplayed()
    composeTestRule.onNodeWithTag(TestTags.START_BUTTON).performClick() // start tracking
    composeTestRule.waitForIdle()
    Thread.sleep(10000) // track for 10 seconds
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
    composeTestRule
        .onAllNodesWithTag("${TestTags.CREATE_ITINERARY_LOCATION}_Placed marker")
        .onFirst()
        .assertExists()
    composeTestRule.onNodeWithTag(TestTags.ADD_LOCATION_BUTTON).performClick()
    composeTestRule.waitForIdle()

    // verify that we have navigated to the search location screen
    composeTestRule.onNodeWithTag(TestTags.LOCATION_SEARCH_SCAFFOLD).assertIsDisplayed()
    composeTestRule.onNodeWithTag(TestTags.LOCATION_SEARCH_BAR).performTextInput(itinerary.title)
    composeTestRule.onNodeWithTag(TestTags.LOCATION_SEARCH_BAR).performImeAction()
    composeTestRule.waitForIdle()
    Thread.sleep(2000L) // wait for result to arrive

    // verify that some results have been found
    composeTestRule.onNodeWithTag(TestTags.LOCATION_SEARCH_RESULTS).assertIsDisplayed()
    composeTestRule.onNodeWithTag(TestTags.LOCATION_SEARCH_RESULTS).performScrollToIndex(0)
    composeTestRule
        .onNodeWithTag("${TestTags.LOCATION_SEARCH_RESULTS}_${location.title}")
        .performClick()
    composeTestRule.waitForIdle()

    // verify that the add location button has appeared and click it
    composeTestRule.onNodeWithTag(TestTags.ADD_LOCATION_BUTTON).assertIsDisplayed()
    composeTestRule.onNodeWithTag(TestTags.ADD_LOCATION_BUTTON).performClick()
    composeTestRule.waitForIdle()

    // verify that we have navigated back to itinerary creation method screen
    composeTestRule.onNodeWithTag(TestTags.CREATE_ITINERARY_MANUALLY_SCREEN).assertIsDisplayed()
    composeTestRule
        .onNodeWithTag("${TestTags.CREATE_ITINERARY_LOCATION}_${location.title}")
        .assertExists()

    // click on next step of itinerary creation
    composeTestRule.onNodeWithTag(TestTags.ITINERARY_CREATION_BAR_DESCRIPTION).performClick()
    composeTestRule.waitForIdle()

    // verify that we have navigated to choose description screen
    composeTestRule.onNodeWithTag(TestTags.CREATION_SCREEN_DESCRIPTION_TITLE).assertIsDisplayed()
    composeTestRule.onNodeWithTag(TestTags.CREATION_SCREEN_TITLE).performTextInput(itinerary.title)
    composeTestRule
        .onNodeWithTag(TestTags.CREATION_SCREEN_DESCRIPTION)
        .performTextInput(itinerary.description!!)
    composeTestRule.onNodeWithTag(TestTags.ITINERARY_CREATION_BAR_TAGS).performClick()
    composeTestRule.waitForIdle()

    // verify that we have navigated to choose tags screen
    composeTestRule.onNodeWithTag(TestTags.CREATION_SCREEN_TAGS).assertIsDisplayed()
    composeTestRule
        .onNodeWithTag(TestTags.ITINERARY_CREATION_PRICE_ESTIMATION)
        .performTextInput("10")
    composeTestRule.onNodeWithTag(TestTags.ITINERARY_CREATION_TIME_ESTIMATION).performTextInput("2")
    composeTestRule.onNodeWithTag(TestTags.ITINERARY_CREATION_TAGS).performClick()
    composeTestRule
        .onNodeWithTag("${TestTags.ITINERARY_CREATION_TAGS}_${itinerary.tags.first()}")
        .performClick() // add adventure tag

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
    composeTestRule
        .onNodeWithTag("${TestTags.ITINERARY_BANNER}_${itinerary.uid}")
        .assertExists() // verify that the created itinerary exists
  }
}
