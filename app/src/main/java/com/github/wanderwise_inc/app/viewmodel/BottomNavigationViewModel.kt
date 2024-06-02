package com.github.wanderwise_inc.app.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

/** Enum representing the items in the bottom navigation bar. */
enum class NavigationItem {
    OVERVIEW,
    LIKED,
    CREATE,
    MAP,
    PROFILE
}

/** ViewModel for managing the state of the bottom navigation bar. */
class BottomNavigationViewModel: ViewModel() {
    /** LiveData for the selected navigation item index. */
    private val _selected = MutableLiveData<Int>()

    /**
     * Publicly exposed LiveData for the selected navigation item index.
     *
     * @return the LiveData containing the index of the selected navigation item.
     */
    val selected: LiveData<Int>
        get() = _selected

    /** Initializes the ViewModel with a default selected navigation item index. */
    init {
        _selected.value = 0
        // Initialize with default value
    }

    /**
     * Sets the selected navigation item index.
     *
     * @param index the index of the navigation item to select.
     */
    fun setSelected(index: Int) {
        _selected.value = index
    }
}
