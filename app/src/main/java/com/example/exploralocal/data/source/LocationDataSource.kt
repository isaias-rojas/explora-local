package com.example.exploralocal.data.source


import android.content.Context
import android.location.Location
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.Tasks
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class LocationDataSource @Inject constructor(
    private val context: Context
) {
    private val fusedLocationClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)

    suspend fun getLastKnownLocation(): Location? = withContext(Dispatchers.IO) {
        try {
            val locationTask = fusedLocationClient.lastLocation
            return@withContext Tasks.await(locationTask)
        } catch (e: SecurityException) {
            // Permission not granted
            return@withContext null
        } catch (e: Exception) {
            // Other error occurred
            return@withContext null
        }
    }
}