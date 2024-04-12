package com.github.wanderwise_inc.app.ui.profile

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.github.wanderwise_inc.app.viewmodel.MapViewModel

@Composable
fun ProfileScreen(
    mapViewModel: MapViewModel
) {
    Text(text = "Welcome, here you will find all the information about your profile")
}