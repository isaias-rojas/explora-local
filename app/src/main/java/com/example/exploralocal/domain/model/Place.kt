package com.example.exploralocal.domain.model

import java.util.UUID

data class Place(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val description: String,
    val rating: Float,
    val latitude: Double,
    val longitude: Double,
    val photoPath: String? = null,
    val createdAt: Long = System.currentTimeMillis()
)