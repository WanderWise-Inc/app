package com.github.wanderwise_inc.app.ui.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.github.wanderwise_inc.app.R

@Composable
fun SearchBar(onSearchChange: (String) -> Unit) {
  var query by remember { mutableStateOf("") }
  OutlinedTextField(
      value = query,
      onValueChange = { s: String ->
        query = s
        onSearchChange(s)
      },
      placeholder = { Text(text = "Wander where?") },
      leadingIcon = {
        Icon( // TODO: make icon appear
            painter = painterResource(id = R.drawable.more_icon),
            contentDescription = null,
            tint = Color.Black,
            modifier =
                Modifier.clickable {
                      // TODO: add filters in drop down menu
                    }
                    .padding(2.dp)
                    .size(30.dp))
      },
      singleLine = true,
      shape = RoundedCornerShape(30.dp),
      modifier =
          Modifier
              // .padding(5.dp)
              .fillMaxWidth()
              .padding(5.dp))
}
