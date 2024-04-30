package com.github.wanderwise_inc.app

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember

@Composable
fun TestingScreen() {
  // Initialize mutable state outside the composable function
  val counter = remember { mutableIntStateOf(0) }

  Column {
    Button(
        onClick = {
          // Update the counter state when INCREASE button is clicked
          counter.intValue += 1
        }) {
          Text(text = "INCREASE")
        }

    Button(
        onClick = {
          // Update the counter state when DECREASE button is clicked
          counter.intValue -= 1
        }) {
          Text(text = "DECREASE")
        }

    // Access the counter state within the composable
    Text(text = counter.intValue.toString())
  }
}
