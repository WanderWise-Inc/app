package com.github.wanderwise_inc.app.ui.creation
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import com.github.wanderwise_inc.app.ui.TestTags
import com.github.wanderwise_inc.app.viewmodel.CreateItineraryViewModel
import com.github.wanderwise_inc.app.viewmodel.ItineraryViewModel
import io.mockk.impl.annotations.MockK
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.junit.MockitoJUnitRunner
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class CreateItineraryMapUITest {

    @get:Rule
    val composeTestRule = createComposeRule()
    

    @MockK
    private lateinit var mockViewModel: CreateItineraryViewModel

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
    }
    @Test
    fun testLocation1TextField() {
        composeTestRule.setContent {
            SelectLocation(mapViewModel = mockViewModel)
        }

        composeTestRule.onNodeWithTag(TestTags.FIRST_LOCATION).assertExists()

        // Here you can add assertions to check if the SelectLocation function is working as expected.
        // For example, you can check if the CreateItineraryMap function is called with the correct ViewModel.
        // However, since Compose doesn't provide a way to directly check this, this is just a placeholder.
    }
    @Test
    fun testLocation2TextField() {
        composeTestRule.setContent {
            SelectLocation(mapViewModel = mockViewModel)
        }

        composeTestRule.onNodeWithTag(TestTags.SECOND_LOCATION).assertExists()
    }
}

