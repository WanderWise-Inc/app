package com.github.wanderwise_inc.app.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel

class HomeViewModel : ViewModel(){
    var backGroundColor by mutableStateOf(Color.White)
        private set

    fun changeColor() {
        backGroundColor = Color.Red
    }
}