package com.github.wanderwise_inc.app.e2e

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.isDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onFirst
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.GrantPermissionRule
import com.github.wanderwise_inc.app.MainActivity
import com.github.wanderwise_inc.app.ui.TestTags
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class PreviewItineraryE2ETest {

  @get:Rule val activityRule = ActivityScenarioRule(MainActivity::class.java)

  @get:Rule val composeTestRule = createAndroidComposeRule<MainActivity>()

  @get:Rule
  val grantPermissionRule: GrantPermissionRule =
      GrantPermissionRule.grant(
          android.Manifest.permission.ACCESS_FINE_LOCATION,
          android.Manifest.permission.ACCESS_COARSE_LOCATION)

  @Test
  fun previewItineraryFlowTest() {
    // Click on the sign in button
    composeTestRule.onNodeWithTag(TestTags.SIGN_IN_BUTTON).performClick()
    composeTestRule.waitUntil {
      composeTestRule.onNodeWithTag(TestTags.OVERVIEW_SCREEN).isDisplayed()
    }

    // Find first itinerary banner
    val firstItineraryBanner =
        composeTestRule.onAllNodes(E2EUtils.hasSubTestTag(TestTags.ITINERARY_BANNER)).onFirst()

    // Click on the first itinerary banner to preview the itinerary
    firstItineraryBanner.assertIsDisplayed()
    firstItineraryBanner.performClick()
    composeTestRule.waitUntil {
      composeTestRule.onNodeWithTag(TestTags.MAP_PREVIEW_ITINERARY_SCREEN).isDisplayed()
    }

    // Check if the maps is displayed
    composeTestRule.onNodeWithTag(TestTags.MAP_GOOGLE_MAPS).assertIsDisplayed()

    // Click on the bottom navigation bar overview button to navigate back to OverviewScreen
    composeTestRule
        .onNodeWithTag(testTag = TestTags.BOTTOM_NAV_OVERVIEW, useUnmergedTree = true)
        .performClick()
    composeTestRule.waitUntil {
      composeTestRule.onNodeWithTag(TestTags.OVERVIEW_SCREEN).isDisplayed()
    }

    // Check if the itinerary banner is still displayed
    firstItineraryBanner.assertIsDisplayed()
  }
}
