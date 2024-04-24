package com.github.wanderwise_inc.app.ui.itinerary

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

@Composable
fun ItineraryChat() {
    var textState by remember { mutableStateOf("") }
    val messages = remember { mutableStateListOf<String>() }
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()


    Scaffold(
        bottomBar = {
            InputBar(
                textState = textState,
                onTextChange = { textState = it },
                onSend = {
                    if (textState.isNotBlank()) {
                        messages.add(textState)
                        textState = ""
                        coroutineScope.launch {
                            listState.animateScrollToItem(messages.size - 1)
                        }
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier.padding(innerPadding)
        ) {
            MessagesList(
                messages = messages,
                listState = listState
            )
        }

    }
}

@Composable
fun MessagesList(messages: List<String>, listState: LazyListState) {
    LazyColumn(
        state = listState,
        modifier = Modifier
            .verticalScroll(rememberScrollState())
            .height(1000.dp)
    ) {
        items(messages.size) { index ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                // Bubble style for messages
                MessageBubble(text = messages[index], isUserMessage = index % 2 == 0)  // Assume alternating messages for demo
            }
        }
    }
}

@Composable
fun MessageBubble(text: String, isUserMessage: Boolean) {
    val backgroundColor = if (isUserMessage) Color(0xFFDCF8C6) else Color(0xFFEAEAEA)
    val alignment = if (isUserMessage) Alignment.CenterEnd else Alignment.CenterStart

    Text(
        text = text,
        modifier = Modifier
            .background(color = backgroundColor, shape = RoundedCornerShape(8.dp))
            .padding(8.dp)
    )
}

@Composable
fun InputBar(
    textState: String,
    onTextChange: (String) -> Unit,
    onSend: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        verticalAlignment = Alignment.Bottom
    ) {
        TextField(
            value = textState,
            onValueChange = onTextChange,
            modifier = Modifier.weight(1f),
            placeholder = { Text("Type a message...") },
            keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(onDone = { onSend() }),
        )
        Button(
            onClick = { onSend() },
            modifier = Modifier.padding(start = 8.dp),
            enabled = textState.isNotBlank()
        ) {
            Icon(imageVector = Icons.AutoMirrored.Default.Send, contentDescription = null)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    MaterialTheme {
        ItineraryChat()
    }
}
