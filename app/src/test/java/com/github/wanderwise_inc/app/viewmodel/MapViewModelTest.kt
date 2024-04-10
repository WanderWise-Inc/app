package com.github.wanderwise_inc.app.viewmodel

import com.github.wanderwise_inc.app.data.ItineraryRepositoryTestImpl
import com.google.firebase.firestore.FirebaseFirestore
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.mock
import org.mockito.MockitoAnnotations

/**
 * @brief test class for mapview model
 */
class MapViewModelTest {
    private lateinit var mapViewModel: MapViewModel

    @Mock
    private lateinit var firestore: FirebaseFirestore

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        firestore = mock(FirebaseFirestore::class.java)
        mapViewModel = MapViewModel(ItineraryRepositoryTestImpl())
    }

    @Test
    fun `add then lookup itinerary`() {
        assert(true)
    }
}