package com.example.exploralocal.data.local.dao

import androidx.room.*
import com.example.exploralocal.data.local.entity.PlaceEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PlaceDao {
    @Query("SELECT * FROM places ORDER BY name ASC")
    fun getPlacesOrderedByName(): Flow<List<PlaceEntity>>

    @Query("SELECT * FROM places ORDER BY rating DESC")
    fun getPlacesOrderedByRating(): Flow<List<PlaceEntity>>

    @Query("SELECT * FROM places WHERE id = :placeId")
    suspend fun getPlaceById(placeId: String): PlaceEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlace(place: PlaceEntity)

    @Update
    suspend fun updatePlace(place: PlaceEntity)

    @Delete
    suspend fun deletePlace(place: PlaceEntity)

    @Query("SELECT * FROM places WHERE " +
            "((latitude BETWEEN :minLat AND :maxLat) AND " +
            "(longitude BETWEEN :minLng AND :maxLng))")
    suspend fun getNearbyPlaces(
        minLat: Double,
        maxLat: Double,
        minLng: Double,
        maxLng: Double
    ): List<PlaceEntity>
}