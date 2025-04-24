package com.example.exploralocal.data.repository

import com.example.exploralocal.data.local.dao.PlaceDao
import com.example.exploralocal.data.local.entity.PlaceEntity
import com.example.exploralocal.domain.model.Place
import com.example.exploralocal.domain.repository.PlaceRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import kotlin.math.*

class PlaceRepositoryImpl @Inject constructor(
    private val placeDao: PlaceDao
) : PlaceRepository {

    override fun getPlacesOrderedByName(): Flow<List<Place>> {
        return placeDao.getPlacesOrderedByName().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getPlacesOrderedByRating(): Flow<List<Place>> {
        return placeDao.getPlacesOrderedByRating().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun getPlaceById(id: String): Place? {
        return placeDao.getPlaceById(id)?.toDomain()
    }

    override suspend fun insertPlace(place: Place) {
        placeDao.insertPlace(PlaceEntity.fromDomain(place))
    }

    override suspend fun updatePlace(place: Place) {
        placeDao.updatePlace(PlaceEntity.fromDomain(place))
    }

    override suspend fun deletePlace(place: Place) {
        placeDao.deletePlace(PlaceEntity.fromDomain(place))
    }

    override suspend fun getNearbyPlaces(
        latitude: Double,
        longitude: Double,
        radiusInKm: Double
    ): List<Place> {
        // Earth's radius in kilometers
        val earthRadius = 6371.0

        // Calculate latitude and longitude bounds
        val latDistance = radiusInKm / earthRadius * (180.0 / Math.PI)
        val lngDistance = radiusInKm / (earthRadius * cos(Math.toRadians(latitude))) * (180.0 / Math.PI)

        val minLat = latitude - latDistance
        val maxLat = latitude + latDistance
        val minLng = longitude - lngDistance
        val maxLng = longitude + lngDistance

        // Get places within bounds
        val boundsPlaces = placeDao.getNearbyPlaces(minLat, maxLat, minLng, maxLng)

        // Filter further to check exact distance and transform to domain model
        return boundsPlaces
            .filter { place ->
                // Calculate actual distance between points
                val distance = calculateDistance(
                    latitude, longitude,
                    place.latitude, place.longitude
                )
                distance <= radiusInKm
            }
            .map { it.toDomain() }
    }

    // Haversine formula to calculate distance between two points
    private fun calculateDistance(
        lat1: Double, lon1: Double,
        lat2: Double, lon2: Double
    ): Double {
        val earthRadius = 6371.0 // kilometers

        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)

        val a = sin(dLat / 2) * sin(dLat / 2) +
                cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) *
                sin(dLon / 2) * sin(dLon / 2)

        val c = 2 * atan2(sqrt(a), sqrt(1 - a))

        return earthRadius * c
    }
}