package com.github.wanderwise_inc.app.ui.itinerary

import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ItineraryChatTest {

  @get:Rule val composeTestRule = createComposeRule()

  @Test
  fun `ItineraryChat should display InputBar when initialized`() {
    composeTestRule.setContent { ItineraryChat() }

    // Verify if the InputBar is present
    composeTestRule.onNodeWithText("Type a message...").assertExists()
  }

  @Test
  fun `MessagesList should display all messages correctly`() {
    composeTestRule.setContent { MessagesList(listOf("Hello", "World"), rememberLazyListState()) }

    // Verify messages are displayed
    composeTestRule.onNodeWithText("Hello").assertExists()
    composeTestRule.onNodeWithText("World").assertExists()
  }

  @Test
  fun `MessageBubble should display the correct message`() {
    composeTestRule.setContent { MessageBubble("User Message", true) }

    // Check text
    composeTestRule.onNodeWithText("User Message").assertExists()
  }

  @Test
  fun `InputBar should send text message when send button is clicked`() {
    var messageSent = false
    composeTestRule.setContent {
      InputBar(textState = "", onTextChange = {}, onSend = { messageSent = true })
    }

    // Simulate typing a message and sending it
    composeTestRule.onNodeWithText("Type a message...").performTextInput("Hello")
    composeTestRule.onNodeWithContentDescription("Send Message").performClick()

    // Assert message was sent
    assert(messageSent)
  }
}
