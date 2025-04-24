package com.example.exploralocal.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.exploralocal.data.local.dao.PlaceDao
import com.example.exploralocal.data.local.entity.PlaceEntity

@Database(
    entities = [PlaceEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun placeDao(): PlaceDao
}