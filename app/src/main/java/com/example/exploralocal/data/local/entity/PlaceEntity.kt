package com.example.exploralocal.data.local.entity


import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.exploralocal.domain.model.Place

@Entity(tableName = "places")
data class PlaceEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val description: String,
    val rating: Float,
    val latitude: Double,
    val longitude: Double,
    val photoPath: String?,
    val createdAt: Long
) {
    companion object {
        fun fromDomain(place: Place): PlaceEntity {
            return PlaceEntity(
                id = place.id,
                name = place.name,
                description = place.description,
                rating = place.rating,
                latitude = place.latitude,
                longitude = place.longitude,
                photoPath = place.photoPath,
                createdAt = place.createdAt
            )
        }
    }

    fun toDomain(): Place {
        return Place(
            id = id,
            name = name,
            description = description,
            rating = rating,
            latitude = latitude,
            longitude = longitude,
            photoPath = photoPath,
            createdAt = createdAt
        )
    }
}