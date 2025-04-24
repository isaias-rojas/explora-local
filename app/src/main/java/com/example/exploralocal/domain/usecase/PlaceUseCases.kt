package com.example.exploralocal.domain.usecase


import com.example.exploralocal.domain.model.Place
import com.example.exploralocal.domain.repository.PlaceRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class PlaceUseCases @Inject constructor(
    private val repository: PlaceRepository
) {
    fun getPlacesByName(): Flow<List<Place>> {
        return repository.getPlacesOrderedByName()
    }

    fun getPlacesByRating(): Flow<List<Place>> {
        return repository.getPlacesOrderedByRating()
    }

    suspend fun getPlaceById(id: String): Place? {
        return repository.getPlaceById(id)
    }

    suspend fun addPlace(place: Place) {
        repository.insertPlace(place)
    }

    suspend fun updatePlace(place: Place) {
        repository.updatePlace(place)
    }

    suspend fun deletePlace(place: Place) {
        repository.deletePlace(place)
    }

    suspend fun getNearbyPlaces(
        latitude: Double,
        longitude: Double,
        radiusInKm: Double = 2.0
    ): List<Place> {
        return repository.getNearbyPlaces(latitude, longitude, radiusInKm)
    }
}