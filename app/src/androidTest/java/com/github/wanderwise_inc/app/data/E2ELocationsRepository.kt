package com.github.wanderwise_inc.app.data

import androidx.lifecycle.LiveData
import com.github.wanderwise_inc.app.model.location.Location

class E2ELocationsRepository : LocationsRepository {
    override fun getPlaces(name: String, limit: Int, apiKey: String): LiveData<List<Location>?> {
        TODO("Not yet implemented")
    }
}