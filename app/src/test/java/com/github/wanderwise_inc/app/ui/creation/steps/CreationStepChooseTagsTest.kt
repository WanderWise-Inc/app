package com.github.wanderwise_inc.app.ui.creation.steps

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performTextInput
import com.github.wanderwise_inc.app.data.ImageRepository
import com.github.wanderwise_inc.app.model.location.Itinerary
import com.github.wanderwise_inc.app.ui.TestTags
import com.github.wanderwise_inc.app.viewmodel.CreateItineraryViewModel
import io.mockk.MockKAnnotations
import io.mockk.Runs
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.just
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class CreationStepChooseTagsTest {
    @get:Rule val composeTestRule = createComposeRule()

    @MockK private lateinit var createItineraryViewModel: CreateItineraryViewModel
    @MockK private lateinit var imageRepository: ImageRepository


    @Before
    fun setup() {
        MockKAnnotations.init(this)

        every { imageRepository.setIsItineraryImage(true) } just Runs
        every { imageRepository.setOnImageSelectedListener(any()) } just Runs

        composeTestRule.setContent {
            CreationStepChooseTagsScreen(createItineraryViewModel = createItineraryViewModel, imageRepository = imageRepository)
        }
    }
}