package com.github.wanderwise_inc.app.e2e

import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.semantics.getOrNull
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.SemanticsNodeInteraction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.isDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithContentDescription
import androidx.compose.ui.test.onFirst
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.GrantPermissionRule
import com.github.wanderwise_inc.app.MainActivity
import com.github.wanderwise_inc.app.ui.TestTags
import org.junit.Assert.assertThrows
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LikeE2ETest {

    @get:Rule
    val activityRule = ActivityScenarioRule(MainActivity::class.java)

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @get:Rule
    val grantPermissionRule: GrantPermissionRule =
        GrantPermissionRule.grant(
            android.Manifest.permission.ACCESS_FINE_LOCATION,
            android.Manifest.permission.ACCESS_COARSE_LOCATION
        )

    @Test
    fun likeFlowTest() {
        // Click on the sign in button
        composeTestRule.onNodeWithTag(TestTags.SIGN_IN_BUTTON).performClick()
        composeTestRule.waitUntil { composeTestRule.onNodeWithTag(TestTags.OVERVIEW_SCREEN).isDisplayed() }

        val firstLikeButton: SemanticsNodeInteraction

        try {
            // Check if there is any like button displayed
            firstLikeButton = composeTestRule
                .onAllNodesWithContentDescription("like button heart icon")
                .onFirst()
        }
        catch (e: AssertionError) {
            // If there is no like button displayed, return
            return
        }

        // Click on the first like button to like the itinerary
        firstLikeButton.assertIsDisplayed()
        firstLikeButton.performClick()
        composeTestRule.waitForIdle()

        // Click on the bottom navigation bar liked button to navigate to LikedScreen
        composeTestRule.onNodeWithTag(TestTags.BOTTOM_NAV_LIKED).performClick()
        composeTestRule.waitForIdle()

        // Check if the liked itinerary banner is displayed
        composeTestRule
            .onAllNodes(hasSubTestTag(TestTags.ITINERARY_BANNER))
            .onFirst()
            .assertIsDisplayed()

        // Check if the liked itinerary banner like button is displayed
        val likedItineraryLikeButton = composeTestRule
            .onAllNodes(hasSubTestTag(TestTags.ITINERARY_BANNER_LIKE_BUTTON))
            .onFirst()
            .assertIsDisplayed()

        // Click on the liked itinerary banner like button to remove the like from the itinerary
        likedItineraryLikeButton.performClick()
        composeTestRule.waitForIdle()

        // Click on the bottom navigation bar overview button to navigate back to OverviewScreen
        composeTestRule.onNodeWithTag(TestTags.BOTTOM_NAV_OVERVIEW).performClick()
        composeTestRule.waitForIdle()

        // Click on the bottom navigation bar liked button to navigate to LikedScreen
        composeTestRule.onNodeWithTag(TestTags.BOTTOM_NAV_LIKED).performClick()
        composeTestRule.waitForIdle()

        // Check if no itinerary banner is displayed
        assertThrows(AssertionError::class.java) {
            composeTestRule
                .onAllNodes(hasSubTestTag(TestTags.ITINERARY_BANNER))
                .onFirst()
        }
    }

    // Function to check if the node has a testTag which contains the given subTestTag
    private fun hasSubTestTag(subTestTag: String): SemanticsMatcher {
        return SemanticsMatcher("Node with subtag $subTestTag") { node ->
            node.config.getOrNull(SemanticsProperties.TestTag)?.contains(subTestTag) == true
        }
    }
}
