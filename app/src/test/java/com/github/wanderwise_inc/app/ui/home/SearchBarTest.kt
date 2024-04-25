package com.github.wanderwise_inc.app.ui.home

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import io.mockk.mockk
import io.mockk.verify
import org.junit.Rule
import org.junit.Test

class SearchBarTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun searchBarRendersCorrectly() {
        composeTestRule.setContent {
            SearchBar(onSearchChange = {}, onPriceChange = {})
        }

        composeTestRule.onNodeWithText("Wander where?").assertExists()
    }

    @Test
    fun searchBarUpdatesStateOnTextInput() {
        var query = ""
        composeTestRule.setContent {
            SearchBar(onSearchChange = { query = it }, onPriceChange = {})
        }

        composeTestRule.onNodeWithText("Wander where?").performTextInput("test")

        assert(query == "test")
    }
}