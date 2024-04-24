package com.github.wanderwise_inc.app.ui.search

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.wanderwise_inc.app.ui.search.TutorialFilterChip



@Composable
fun ChipsScreen() {
    Column(Modifier.fillMaxSize()) {


        Text(text = "Filter Chip", fontSize = 24.sp, fontWeight = FontWeight.Bold)
        TutorialFilterChip()
        Spacer(modifier = Modifier.padding(vertical = 10.dp))

    }
}