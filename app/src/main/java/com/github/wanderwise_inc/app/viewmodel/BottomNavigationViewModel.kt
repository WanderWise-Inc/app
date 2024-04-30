package com.github.wanderwise_inc.app.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

// not nice to use in the class
enum class NavigationItem {
  OVERVIEW,
  LIKED,
  CREATE,
  MAP,
  PROFILE
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
  public val selected: LiveData<Int>
    get() = _selected

  init {
    _selected.value = 0 // Initialize with default value
  }

  fun setSelected(index: Int) {
    _selected.value = index
  }
}
