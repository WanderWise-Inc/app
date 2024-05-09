package com.github.wanderwise_inc.app.ui.creation





import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CheckBox
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun CreationFinishButton(onFinished: ()->Unit) {
        ExtendedFloatingActionButton(
            onClick = { onFinished() },
            icon = { Icon(Icons.Outlined.CheckBox, "finished") },
            text = { Text("Finish") },
            containerColor = MaterialTheme.colorScheme.secondaryContainer,
            contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
//            modifier = Modifier.padding(16.dp).align(Aligment)
        )



}