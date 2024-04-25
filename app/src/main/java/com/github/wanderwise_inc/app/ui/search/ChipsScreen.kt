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
import com.github.wanderwise_inc.app.ui.search.FilterChip



@Composable
fun ChipsScreen() {
    Column(Modifier.fillMaxSize()) {


        Text(text = "Filter Your Itinerary", fontSize = 24.sp, fontWeight = FontWeight.Bold)
        FilterChip()
        //ChipWithSubItems(chipLabel = "Price Range", chipItems = listOf("0-20$", "20-40$", "40-+$"))
        Spacer(modifier = Modifier.padding(vertical = 10.dp))

    }
}