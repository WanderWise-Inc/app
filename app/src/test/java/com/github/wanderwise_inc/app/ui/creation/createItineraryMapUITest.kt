package com.github.wanderwise_inc.app.ui.creation
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import com.github.wanderwise_inc.app.ui.TestTags
import com.github.wanderwise_inc.app.viewmodel.CreateItineraryViewModel
import com.github.wanderwise_inc.app.viewmodel.ItineraryViewModel
import io.mockk.MockKAnnotations
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
        MockKAnnotations.init(this)


    }

    @Test
    fun dummy() {
        assert(true)
    }

    @Test
    fun testLocation1TextField() {
        composeTestRule.setContent {
            SelectLocation(mapViewModel = mockViewModel)
        }

        composeTestRule.onNodeWithTag(TestTags.FIRST_LOCATION).assertIsDisplayed()

    }
    @Test
    fun testLocation2TextField() {
        composeTestRule.setContent {
            SelectLocation(mapViewModel = mockViewModel)
        }

        composeTestRule.onNodeWithTag(TestTags.SECOND_LOCATION).assertIsDisplayed()
    }
}

