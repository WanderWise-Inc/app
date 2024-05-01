package com.github.wanderwise_inc.app.ui.itinerary

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.wanderwise_inc.app.R
import kotlinx.coroutines.launch

/** @brief simple chat UI */
@Composable
fun ItineraryChat() {
  var textState by remember { mutableStateOf("") }
  val messagesState = remember { mutableStateListOf<String>() }
  val listState = rememberLazyListState()
  val coroutineScope = rememberCoroutineScope()

  Scaffold(
      bottomBar = {
        InputBar(
            textState = textState,
            onTextChange = { textState = it },
            onSend = {
              if (textState.isNotBlank()) {
                messagesState.add(textState)
                textState = ""
                coroutineScope.launch { listState.animateScrollToItem(messagesState.size - 1) }
              }
            })
      },
      containerColor = MaterialTheme.colorScheme.background) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)) {
          MessagesList(messagesState = messagesState, listState = listState)
        }
      }
}

/** @brief message history */
@Composable
fun MessagesList(messagesState: List<String>, listState: LazyListState) {
  LazyColumn(
      state = listState,
      modifier =
          Modifier.verticalScroll(rememberScrollState())
              .height(LocalConfiguration.current.screenHeightDp.dp)
              .fillMaxWidth()) {
        items(messagesState.size) { index ->
          MessageBubble(message = messagesState[index], isUserMessage = index % 2 == 0)
        }
      }
}

/** @brief message container */
@Composable
fun MessageBubble(message: String, isUserMessage: Boolean) {
  Row(
      modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 4.dp),
      horizontalArrangement = if (isUserMessage) Arrangement.End else Arrangement.Start) {
        Box(
            contentAlignment = Alignment.Center,
            modifier =
                Modifier.background(
                        color =
                            if (isUserMessage) MaterialTheme.colorScheme.primaryContainer
                            else MaterialTheme.colorScheme.secondaryContainer,
                        shape = RoundedCornerShape(12.dp))
                    .padding(8.dp)) {
              Text(
                  text = message,
                  fontSize = 16.sp,
                  color =
                      if (isUserMessage) MaterialTheme.colorScheme.onPrimaryContainer
                      else MaterialTheme.colorScheme.onSecondaryContainer)
            }
      }
}

/** @brief input text field */
@Composable
fun InputBar(textState: String, onTextChange: (String) -> Unit, onSend: () -> Unit) {
  Row(modifier = Modifier.fillMaxWidth().padding(8.dp), verticalAlignment = Alignment.Bottom) {
    TextField(
        value = textState,
        onValueChange = onTextChange,
        modifier = Modifier.weight(1f),
        placeholder = { Text(text = "Type a message...", color = Color.DarkGray) },
        trailingIcon = {
          IconButton(onClick = onSend) {
            Icon(
                painter = painterResource(R.drawable.send_icon),
                contentDescription = "Send Message",
                tint =
                    if (textState.isBlank()) Color.Gray
                    else MaterialTheme.colorScheme.onPrimaryContainer)
          }
        },
        keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
        keyboardActions = KeyboardActions(onDone = { onSend() }),
        colors =
            TextFieldDefaults.colors(
                focusedTextColor = Color.Black,
                unfocusedTextColor = Color.Black,
                focusedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                unfocusedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                cursorColor = Color.DarkGray,
                focusedIndicatorColor = MaterialTheme.colorScheme.onPrimaryContainer,
                unfocusedIndicatorColor = MaterialTheme.colorScheme.onPrimaryContainer))
  }
}
