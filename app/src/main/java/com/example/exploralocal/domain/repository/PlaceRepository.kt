package com.example.exploralocal.domain.repository

import com.example.exploralocal.domain.model.Place
import kotlinx.coroutines.flow.Flow

interface PlaceRepository {
    fun getPlacesOrderedByName(): Flow<List<Place>>
    fun getPlacesOrderedByRating(): Flow<List<Place>>
    suspend fun getPlaceById(id: String): Place?
    suspend fun insertPlace(place: Place)
    suspend fun updatePlace(place: Place)
    suspend fun deletePlace(place: Place)
    suspend fun getNearbyPlaces(
        latitude: Double,
        longitude: Double,
        radiusInKm: Double
    ): List<Place>
}