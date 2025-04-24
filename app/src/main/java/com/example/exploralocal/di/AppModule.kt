package com.example.exploralocal.di


import android.content.Context
import androidx.room.Room
import com.example.exploralocal.data.local.dao.PlaceDao
import com.example.exploralocal.data.local.database.AppDatabase
import com.example.exploralocal.data.repository.PlaceRepositoryImpl
import com.example.exploralocal.domain.repository.PlaceRepository
import com.example.exploralocal.domain.usecase.PlaceUseCases
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "exploralocal_db"
        ).build()
    }

    @Provides
    @Singleton
    fun providePlaceDao(database: AppDatabase): PlaceDao {
        return database.placeDao()
    }

    @Provides
    @Singleton
    fun providePlaceRepository(placeDao: PlaceDao): PlaceRepository {
        return PlaceRepositoryImpl(placeDao)
    }

    @Provides
    @Singleton
    fun providePlaceUseCases(repository: PlaceRepository): PlaceUseCases {
        return PlaceUseCases(repository)
    }
}