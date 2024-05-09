package com.github.wanderwise_inc.app.ui.popup

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Icon
import androidx.compose.material3.Snackbar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

/**
 * Displays a hint popup
 *
 * @param message the message to be displayed
 * @param onDismiss called when "dismiss" button is pressed. Should close popup
 *
 * ```
 * // Usage Example
 * var isHintPopupOpen by remember { mutableStateOf(true) }
 * if (isHintPopupOpen) {
 *   HintPopup("This is a popup message!") {
 *    isHintPopupOpen = false
 *   }
 * }
 * ```
 */
@Composable
fun HintPopup(
    message: String,
    onDismiss: () -> Unit,
) {
  Snackbar(
      action = { Text(text = "Dismiss", modifier = Modifier.clickable { onDismiss() }) },
      modifier = Modifier.padding(16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start) {
              Icon(imageVector = Icons.Filled.Info, contentDescription = "Hint")
              Spacer(modifier = Modifier.width(10.dp))
              Text(text = message)
            }
      }
}

@Preview
@Composable
fun HintPopupPreview() {
  HintPopup(message = "Try tapping your screen to add waypoints") {}
}
