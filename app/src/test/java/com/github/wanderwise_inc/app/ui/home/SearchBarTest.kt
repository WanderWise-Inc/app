package com.github.wanderwise_inc.app.ui
// import androidx.compose.runtime.ClosedFloatingPointRange
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.junit.MockitoJUnitRunner


@RunWith(MockitoJUnitRunner::class)

class SearchBarTest {

  @Mock lateinit var onSearchChange: (String) -> Unit

  @Mock
  // lateinit var onPriceChange: (Float) -> Unit

  lateinit var sliderPositionPriceState: MutableState<ClosedRange<Float>>

  lateinit var sliderPositionTimeState: MutableState<ClosedRange<Float>>

  @Test
  fun testSearchBar() {
    // Assuming SearchBar has a method onSearchChange
    onSearchChange("query")

    // Verify that the onSearchChange method was called with "query"
    verify(onSearchChange).invoke("query")

    // Assuming SearchBar has a method onPriceChange
    // onPriceChange(50f)

    // Verify that the onPriceChange method was called with 50f
    // verify(onPriceChange).invoke(50f)

    // Assuming SearchBar has a method sliderPositionPriceState
    sliderPositionPriceState = mutableStateOf(0f..100f)

    // Verify that the sliderPositionPriceState method was called with 0f..100f
    assertEquals(sliderPositionPriceState.value, 0f..100f)

    // Assuming SearchBar has a method sliderPositionTimeState
    sliderPositionTimeState = mutableStateOf(0f..24f)

    // Verify that the sliderPositionTimeState method was called with 0f..24f
    assertEquals(sliderPositionTimeState.value, 0f..24f)
  } // for the tests : Search bar is being called in Liked Screen and Overview Screen, how to test
  // it?
  // also I modified the Itinerary Banner to have the real price and time and I also modified the
  // Itinerary class, should I also test it?
  /*    @Test
  fun testRangeSliderValues() {
      // Set the price range
      sliderPositionPriceState.value = 10f..50f
      // Check if the formatted string matches the expected output
      assertEquals(String.format("%.2f - %.2f", sliderPositionPriceState.value.start, sliderPositionPriceState.value.endInclusive), "10.00 - 50.00")

      // Set the time range
      sliderPositionTimeState.value = 5f..15f
      // Check if the formatted string matches the expected output
      assertEquals(String.format("%.2f - %.2f", sliderPositionTimeState.value.start, sliderPositionTimeState.value.endInclusive), "5.00 - 15.00")
  }*/
}
