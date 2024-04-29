package com.github.wanderwise_inc.app.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

//not nice to use in the class
enum class NavigationItem {
    OVERVIEW, LIKED, CREATE, MAP, PROFILE
}
class BottomNavigationViewModel {
//    private var selected: Int = 0;
//
//    fun setSelected(index : Int){
//        selected = index
//    }
//
//    fun getSelected(): Int {
//        return selected
//    }
    private val _selected = MutableLiveData<Int>()
    private val selected: LiveData<Int> = _selected

    init {
        _selected.value = 0 // Initialize with default value
    }

    fun getSelected(): MutableLiveData<Int> {
        return _selected
    }

    fun setSelected(index: Int) {
        _selected.value = index
    }
}